package com.gitgrass.global.client

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

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
}
