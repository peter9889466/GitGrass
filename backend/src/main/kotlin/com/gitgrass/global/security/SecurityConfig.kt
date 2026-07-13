package com.gitgrass.global.security

import com.gitgrass.global.security.jwt.JwtAuthenticationFilter
import com.gitgrass.global.security.jwt.JwtTokenProvider
import com.gitgrass.global.security.oauth.CustomOAuth2UserService
import com.gitgrass.global.security.oauth.OAuth2SuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            cors {
                configurationSource = corsConfigurationSource()
            }
            csrf { disable() }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            exceptionHandling {
                authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
            }
            authorizeHttpRequests {
                authorize("/login/**", permitAll)
                authorize("/oauth2/**", permitAll)
                authorize("/error", permitAll)
                authorize("/api/**", authenticated)
                authorize(anyRequest, permitAll)
            }
            oauth2Login {
                userInfoEndpoint {
                    userService = customOAuth2UserService
                }
                authenticationSuccessHandler = oAuth2SuccessHandler
            }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(JwtAuthenticationFilter(jwtTokenProvider))
        }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:5173", "http://localhost:5174")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}

