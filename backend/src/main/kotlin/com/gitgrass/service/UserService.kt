package com.gitgrass.service

import com.gitgrass.domain.OauthProvider
import com.gitgrass.domain.User
import com.gitgrass.global.client.GithubClient
import com.gitgrass.global.client.ContributionCalendar
import com.gitgrass.repository.OauthAccountRepository
import com.gitgrass.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class DiscordConfigResponse(
    val webhookUrl: String?,
    val alertTime: String,
    val isActive: Boolean
)

data class DiscordConfigRequest(
    val webhookUrl: String,
    val alertTime: String,
    val isActive: Boolean
)

data class ContributionDayDTO(
    val color: String,
    val contributionCount: Int,
    val date: String,
    val weekday: Int
)

data class ContributionWeekDTO(
    val contributionDays: List<ContributionDayDTO>
)

data class ContributionCalendarResponseDTO(
    val totalContributions: Int,
    val weeks: List<ContributionWeekDTO>
) {
    companion object {
        fun from(calendar: ContributionCalendar) = ContributionCalendarResponseDTO(
            totalContributions = calendar.totalContributions,
            weeks = calendar.weeks.map { week ->
                ContributionWeekDTO(
                    contributionDays = week.contributionDays.map { day ->
                        ContributionDayDTO(
                            color = day.color,
                            contributionCount = day.contributionCount,
                            date = day.date,
                            weekday = day.weekday
                        )
                    }
                )
            }
        )
    }
}

@Service
class UserService(
    private val userRepository: UserRepository,
    private val oauthAccountRepository: OauthAccountRepository,
    private val githubClient: GithubClient
) {

    @Transactional(readOnly = true)
    fun getDiscordConfig(userId: Long): DiscordConfigResponse {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        return DiscordConfigResponse(
            webhookUrl = user.discordWebhookUrl,
            alertTime = user.discordAlertTime ?: "22:00",
            isActive = user.discordAlertActive
        )
    }

    @Transactional
    fun updateDiscordConfig(userId: Long, request: DiscordConfigRequest): DiscordConfigResponse {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        
        // 간단한 웹훅 형식 검증
        if (request.isActive && !request.webhookUrl.startsWith("https://discord.com/api/webhooks/")) {
            throw IllegalArgumentException("Invalid Discord Webhook URL format")
        }

        user.discordWebhookUrl = request.webhookUrl
        user.discordAlertTime = request.alertTime
        user.discordAlertActive = request.isActive
        
        val saved = userRepository.save(user)
        return DiscordConfigResponse(
            webhookUrl = saved.discordWebhookUrl,
            alertTime = saved.discordAlertTime ?: "22:00",
            isActive = saved.discordAlertActive
        )
    }

    @Transactional
    fun deleteDiscordConfig(userId: Long): DiscordConfigResponse {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        
        user.discordWebhookUrl = null
        user.discordAlertTime = "22:00"
        user.discordAlertActive = false
        
        val saved = userRepository.save(user)
        return DiscordConfigResponse(
            webhookUrl = saved.discordWebhookUrl,
            alertTime = saved.discordAlertTime ?: "22:00",
            isActive = saved.discordAlertActive
        )
    }

    @Transactional(readOnly = true)
    fun getContributions(userId: Long): ContributionCalendarResponseDTO {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        
        val githubAccount = user.oauthAccounts.firstOrNull { it.provider == OauthProvider.GITHUB }
            ?: throw IllegalStateException("GitHub account is not linked")
            
        val oauthAccount = oauthAccountRepository.findByProviderAndProviderUserId(
            OauthProvider.GITHUB,
            githubAccount.providerUserId
        ) ?: throw IllegalStateException("GitHub token not found")
        
        val calendar = githubClient.getContributionCalendar(oauthAccount.accessToken)
        return ContributionCalendarResponseDTO.from(calendar)
    }
}
