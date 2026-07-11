package com.gitgrass.global.security.oauth

import com.gitgrass.global.security.jwt.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class OAuth2SuccessHandler(
    private val jwtTokenProvider: JwtTokenProvider
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val dbUserId = oAuth2User.attributes["db_user_id"]?.toString()?.toLong()
            ?: throw IllegalStateException("Database user ID not found in OAuth2 attributes")
        val email = oAuth2User.attributes["email"]?.toString() ?: ""

        val token = jwtTokenProvider.createToken(dbUserId, email)

        // 프론트엔드 React 개발 서버 URL로 토큰과 함께 리다이렉트
        val targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173")
            .queryParam("token", token)
            .build().toUriString()

        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}
