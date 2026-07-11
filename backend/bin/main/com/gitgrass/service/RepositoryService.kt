package com.gitgrass.service

import com.gitgrass.domain.OauthProvider
import com.gitgrass.domain.Repository
import com.gitgrass.global.client.GithubClient
import com.gitgrass.repository.OauthAccountRepository
import com.gitgrass.repository.RepositoryRepository
import com.gitgrass.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class RepositoryService(
    private val userRepository: UserRepository,
    private val oauthAccountRepository: OauthAccountRepository,
    private val repositoryRepository: RepositoryRepository,
    private val githubClient: GithubClient
) {

    @Transactional
    fun syncAndGetRepositories(userId: Long): List<Repository> {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        
        val githubProviderUserId = user.oauthAccounts.firstOrNull { it.provider == OauthProvider.GITHUB }?.providerUserId
            ?: throw IllegalStateException("GitHub OAuth Account not linked")

        val oauthAccount = oauthAccountRepository.findByProviderAndProviderUserId(
            OauthProvider.GITHUB,
            githubProviderUserId
        ) ?: throw IllegalStateException("GitHub token not found")

        val githubRepos = githubClient.getUserRepositories(oauthAccount.accessToken)

        val existingRepos = repositoryRepository.findAllByUserId(userId).associateBy { it.githubRepoId }
        
        githubRepos.forEach { gRepo ->
            val lastCommitTime = try {
                ZonedDateTime.parse(gRepo.updated_at).toLocalDateTime()
            } catch (e: Exception) {
                null
            }

            val existing = existingRepos[gRepo.id]
            if (existing != null) {
                existing.lastCommitAt = lastCommitTime
                repositoryRepository.save(existing)
            } else {
                val newRepo = Repository(
                    user = user,
                    githubRepoId = gRepo.id,
                    name = gRepo.name,
                    owner = gRepo.owner.login,
                    lastCommitAt = lastCommitTime,
                    isMonitored = true
                )
                repositoryRepository.save(newRepo)
            }
        }

        return repositoryRepository.findAllByUserId(userId)
    }

    @Transactional
    fun toggleMonitoring(userId: Long, repoId: Long, isMonitored: Boolean): Repository {
        val repo = repositoryRepository.findById(repoId).orElseThrow { IllegalArgumentException("Repository not found") }
        if (repo.user.id != userId) {
            throw IllegalAccessException("Access denied for this repository")
        }
        repo.isMonitored = isMonitored
        return repositoryRepository.save(repo)
    }
}
