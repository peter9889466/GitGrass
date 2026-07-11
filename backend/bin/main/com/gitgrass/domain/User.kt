package com.gitgrass.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.CreationTimestamp

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var nickname: String,

    @Column(name = "discord_webhook_url", length = 500)
    var discordWebhookUrl: String? = null,

    @Column(name = "discord_alert_time", length = 10)
    var discordAlertTime: String? = "22:00",

    @Column(name = "discord_alert_active", nullable = false)
    var discordAlertActive: Boolean = false,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val oauthAccounts: MutableList<OauthAccount> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val repositories: MutableList<Repository> = mutableListOf(),

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun addOauthAccount(oauthAccount: OauthAccount) {
        oauthAccounts.add(oauthAccount)
        oauthAccount.user = this
    }

    fun addRepository(repository: Repository) {
        repositories.add(repository)
        repository.user = this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as User
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}
