package com.gitgrass.controller

import com.gitgrass.service.DiscordConfigRequest
import com.gitgrass.service.DiscordConfigResponse
import com.gitgrass.service.ContributionCalendarResponseDTO
import com.gitgrass.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users/me")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/discord")
    fun getDiscordConfig(
        @AuthenticationPrincipal principal: User
    ): ResponseEntity<DiscordConfigResponse> {
        val userId = principal.username.toLong()
        val config = userService.getDiscordConfig(userId)
        return ResponseEntity.ok(config)
    }

    @PutMapping("/discord")
    fun updateDiscordConfig(
        @AuthenticationPrincipal principal: User,
        @RequestBody request: DiscordConfigRequest
    ): ResponseEntity<DiscordConfigResponse> {
        val userId = principal.username.toLong()
        val updated = userService.updateDiscordConfig(userId, request)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/discord")
    fun deleteDiscordConfig(
        @AuthenticationPrincipal principal: User
    ): ResponseEntity<DiscordConfigResponse> {
        val userId = principal.username.toLong()
        val deleted = userService.deleteDiscordConfig(userId)
        return ResponseEntity.ok(deleted)
    }

    @GetMapping("/contributions")
    fun getContributions(
        @AuthenticationPrincipal principal: User
    ): ResponseEntity<ContributionCalendarResponseDTO> {
        val userId = principal.username.toLong()
        val contributions = userService.getContributions(userId)
        return ResponseEntity.ok(contributions)
    }
}
