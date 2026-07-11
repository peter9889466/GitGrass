package com.gitgrass.domain

import com.gitgrass.global.security.crypto.TokenConverter
import jakarta.persistence.*
import java.time.LocalDateTime

enum class OauthProvider {
    GITHUB, DISCORD
}

@Entity
@Table(name = "oauth_accounts", uniqueConstraints = [
    UniqueConstraint(columnNames = ["provider", "provider_user_id"])
])
class OauthAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: OauthProvider,

    @Column(name = "provider_user_id", nullable = false)
    val providerUserId: String,

    @Convert(converter = TokenConverter::class)
    @Column(name = "access_token", nullable = false, length = 1000)
    var accessToken: String,

    @Convert(converter = TokenConverter::class)
    @Column(name = "refresh_token", length = 1000)
    var refreshToken: String? = null,

    @Column(name = "expired_at")
    var expiredAt: LocalDateTime? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as OauthAccount
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}
