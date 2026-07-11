package com.gitgrass.controller

import com.gitgrass.domain.Repository
import com.gitgrass.service.RepositoryService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

data class RepositoryResponseDTO(
    val id: Long,
    val name: String,
    val owner: String,
    val lastCommitAt: LocalDateTime?,
    val isMonitored: Boolean
) {
    companion object {
        fun from(entity: Repository) = RepositoryResponseDTO(
            id = entity.id!!,
            name = entity.name,
            owner = entity.owner,
            lastCommitAt = entity.lastCommitAt,
            isMonitored = entity.isMonitored
        )
    }
}

data class MonitorToggleRequest(
    val isMonitored: Boolean
)

@RestController
@RequestMapping("/api/v1/repositories")
class RepositoryController(
    private val repositoryService: RepositoryService
) {

    @GetMapping
    fun getRepositories(
        @AuthenticationPrincipal principal: User
    ): ResponseEntity<List<RepositoryResponseDTO>> {
        val userId = principal.username.toLong()
        val repos = repositoryService.syncAndGetRepositories(userId)
        val response = repos.map { RepositoryResponseDTO.from(it) }
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{id}/monitor")
    fun toggleRepositoryMonitoring(
        @AuthenticationPrincipal principal: User,
        @PathVariable id: Long,
        @RequestBody request: MonitorToggleRequest
    ): ResponseEntity<RepositoryResponseDTO> {
        val userId = principal.username.toLong()
        val updated = repositoryService.toggleMonitoring(userId, id, request.isMonitored)
        return ResponseEntity.ok(RepositoryResponseDTO.from(updated))
    }
}
