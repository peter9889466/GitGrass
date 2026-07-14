package com.gitgrass.service

import com.gitgrass.domain.OauthProvider
import com.gitgrass.domain.Repository
import com.gitgrass.global.client.GithubClient
import com.gitgrass.repository.RepositoryRepository
import com.gitgrass.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Service
class DiscordSchedulerService(
    private val userRepository: UserRepository,
    private val repositoryRepository: RepositoryRepository,
    private val githubClient: GithubClient
) {
    private val log = LoggerFactory.getLogger(DiscordSchedulerService::class.java)
    private val restClient = RestClient.builder().build()

    // 매 분 0초에 구동 (매 분마다 정교한 알림 검사 수행)
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 * * * * *")
    fun checkAndSendAlerts() {

        log.info("Starting scheduled Discord commit alert checks...")
        
        // 현재 한국(KST) 시각 구하기
        val nowKst = ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
        val currentTimeString = nowKst.format(DateTimeFormatter.ofPattern("HH:mm"))
        
        log.info("Current target alert time: {}", currentTimeString)


        // 현재 시각이 마감 시각인 알림 활성 유저 검색
        val users = userRepository.findAllByDiscordAlertActiveTrueAndDiscordAlertTime(currentTimeString)
        if (users.isEmpty()) {
            log.info("No users registered for alert time: {}", currentTimeString)
            return
        }

        // 한국 시간 기준 오늘 자정의 타임스탬프 구하기
        val midnightKst = nowKst.truncatedTo(ChronoUnit.DAYS)
        // 깃허브 API 조회를 위해 UTC ISO 8601 Instant 문자열로 변환 (예: 2026-07-12T00:00:00Z)
        val sinceIsoString = midnightKst.toInstant().toString()

        for (user in users) {
            try {
                processUserAlert(user, sinceIsoString)
            } catch (e: Exception) {
                log.error("Failed to process Discord alert for user: ${user.nickname} (ID: ${user.id})", e)
            }
        }
    }

    private fun processUserAlert(user: com.gitgrass.domain.User, sinceIsoString: String) {
        val githubAccount = user.oauthAccounts.firstOrNull { it.provider == OauthProvider.GITHUB }
        if (githubAccount == null) {
            log.warn("User ${user.nickname} (ID: ${user.id}) has active Discord alert but GITHUB account is not linked")
            return
        }

        val monitoredRepos = repositoryRepository.findAllByUserIdAndIsMonitoredTrue(user.id!!)
        if (monitoredRepos.isEmpty()) {
            log.info("User ${user.nickname} has no repositories marked as monitored. Skipping alert.")
            return
        }

        val uncommittedRepos = mutableListOf<Repository>()

        // 각 모니터링 대상 리포지토리별로 오늘 커밋이 발생했는지 깃허브 API를 통해 조회
        for (repo in monitoredRepos) {
            val hasCommit = githubClient.hasCommitsSince(
                accessToken = githubAccount.accessToken,
                owner = repo.owner,
                repo = repo.name,
                sinceIsoString = sinceIsoString
            )
            if (!hasCommit) {
                uncommittedRepos.add(repo)
            }
        }


        if (uncommittedRepos.isNotEmpty()) {
            sendDiscordWebhook(user.discordWebhookUrl!!, user.nickname, user.discordAlertTime!!, uncommittedRepos)
        } else {
            log.info("User ${user.nickname} completed all commits today. No alert needed.")
        }

    }

    private fun sendDiscordWebhook(
        webhookUrl: String,
        nickname: String,
        alertTime: String,
        uncommittedRepos: List<Repository>
    ) {
        // 디스코드 Embeds 필드 제한(1024자)을 피하기 위해 표시 개수를 최대 5개로 제한
        val maxToShow = 5
        val displayedRepos = uncommittedRepos.take(maxToShow)
        var repoListMarkdown = displayedRepos.joinToString("\n") { repo ->
            "- [${repo.name}](https://github.com/${repo.owner}/${repo.name}) (마지막 커밋: ${repo.lastCommitAt?.toLocalDate() ?: "없음"})"
        }
        if (uncommittedRepos.size > maxToShow) {
            repoListMarkdown += "\n- 외 ${uncommittedRepos.size - maxToShow}개의 저장소..."
        }

        val payload = mapOf(

            "content" to "🌿 **${nickname}님, 오늘 잔디를 심으셨나요? 마감 시간이 임박했습니다!**",
            "embeds" to listOf(
                mapOf(
                    "title" to "GitGrass 미완료 잔디 알림 ⚠️",
                    "description" to "지정하신 마감 시간(${alertTime})이 도달했으나, 아래 리포지토리에 금일 커밋 내역이 확인되지 않았습니다. 지금 바로 잔디를 채워 연속 커밋 기록을 지켜내세요!",
                    "color" to 16723296, // Neon Orange-Red (#FF2E00)
                    "fields" to listOf(
                        mapOf(
                            "name" to "커밋 대기 리포지토리 목록 (모니터링 중)",
                            "value" to repoListMarkdown
                        )
                    ),
                    "footer" to mapOf(
                        "text" to "GitGrass 🌿 | 잔디 상시 감시 중",
                        "icon_url" to "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png"
                    )
                )
            )
        )

        try {
            val response = restClient.post()
                .uri(webhookUrl)
                .body(payload)
                .retrieve()
                .toBodilessEntity()

            if (response.statusCode.is2xxSuccessful) {
                log.info("Successfully sent Discord alert to user {}", nickname)
            } else {
                log.error("Failed to send Discord alert, status code: {}", response.statusCode)
            }
        } catch (e: Exception) {
            log.error("Error occurred while sending Discord webhook notification to $nickname", e)
        }
    }
}
