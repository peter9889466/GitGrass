package com.gitgrass.repository

import com.gitgrass.domain.Repository
import org.springframework.data.jpa.repository.JpaRepository

interface RepositoryRepository : JpaRepository<Repository, Long> {
    fun findAllByUserId(userId: Long): List<Repository>
    fun findAllByUserIdAndIsMonitoredTrue(userId: Long): List<Repository>
}
