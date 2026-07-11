package com.gitgrass.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "repositories")
class Repository(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(name = "github_repo_id", nullable = false)
    val githubRepoId: Long,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val owner: String,

    @Column(name = "last_commit_at")
    var lastCommitAt: LocalDateTime? = null,

    @Column(name = "is_monitored", nullable = false)
    var isMonitored: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Repository
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}
