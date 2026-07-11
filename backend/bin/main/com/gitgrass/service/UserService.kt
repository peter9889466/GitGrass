package com.gitgrass.service

import com.gitgrass.domain.User
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

@Service
class UserService(
    private val userRepository: UserRepository
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
}
