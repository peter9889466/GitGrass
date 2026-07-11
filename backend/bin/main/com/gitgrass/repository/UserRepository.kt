package com.gitgrass.repository

import com.gitgrass.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findAllByDiscordAlertActiveTrueAndDiscordAlertTime(discordAlertTime: String): List<User>
}
