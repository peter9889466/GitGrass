package com.gitgrass.global.client

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

data class GithubCommitAuthor(
    val date: String
)

data class GithubCommitInfo(
    val author: GithubCommitAuthor
)

data class GithubCommitResponse(
    val sha: String,
    val commit: GithubCommitInfo
)

data class GithubOwner(
    val login: String
)

data class GithubRepoResponse(
    val id: Long,
    val name: String,
    val owner: GithubOwner,
    val updated_at: String
)

@Component
class GithubClient {

    private val restClient = RestClient.builder()
        .baseUrl("https://api.github.com")
        .build()

    fun getUserRepositories(accessToken: String): List<GithubRepoResponse> {
        val response = restClient.get()
            .uri("/user/repos?visibility=all&affiliation=owner,collaborator")
            .header("Authorization", "Bearer $accessToken")
            .header("Accept", "application/vnd.github+json")
            .retrieve()
            .toEntity(Array<GithubRepoResponse>::class.java)

        return response.body?.toList() ?: emptyList()
    }

    fun hasCommitsSince(accessToken: String, owner: String, repo: String, sinceIsoString: String): Boolean {
        return try {
            val response = restClient.get()
                .uri("/repos/$owner/$repo/commits?since=$sinceIsoString")
                .header("Authorization", "Bearer $accessToken")
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .toEntity(Array<GithubCommitResponse>::class.java)
            
            val commits = response.body?.toList() ?: emptyList()
            commits.isNotEmpty()
        } catch (e: Exception) {
            // API 오류(예: 빈 리포지토리 등)의 경우 false 반환
            false
        }
    }
}
