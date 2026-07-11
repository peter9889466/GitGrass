package com.gitgrass.global.security.oauth

import com.gitgrass.domain.OauthAccount
import com.gitgrass.domain.OauthProvider
import com.gitgrass.domain.User
import com.gitgrass.repository.OauthAccountRepository
import com.gitgrass.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Collections

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
    private val oauthAccountRepository: OauthAccountRepository
) : DefaultOAuth2UserService() {

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        
        val registrationId = userRequest.clientRegistration.registrationId.uppercase()
        val provider = OauthProvider.valueOf(registrationId)
        val userNameAttributeName = userRequest.clientRegistration.providerDetails.userInfoEndpoint.userNameAttributeName
        
        val attributes = oAuth2User.attributes
        val providerUserId = attributes["id"]?.toString() ?: throw IllegalArgumentException("OAuth2 User ID not found")
        val nickname = attributes["login"]?.toString() ?: attributes["name"]?.toString() ?: "User"
        val email = attributes["email"]?.toString() ?: "$nickname@gitgrass.com"
        
        val accessToken = userRequest.accessToken.tokenValue
        
        val existingAccount = oauthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId)
        val user = if (existingAccount != null) {
            val existingUser = existingAccount.user
            existingUser.nickname = nickname
            existingUser.email = email
            existingAccount.accessToken = accessToken
            userRepository.save(existingUser)
        } else {
            val newUser = User(
                email = email,
                nickname = nickname
            )
            val newAccount = OauthAccount(
                user = newUser,
                provider = provider,
                providerUserId = providerUserId,
                accessToken = accessToken
            )
            newUser.addOauthAccount(newAccount)
            userRepository.save(newUser)
        }

        val customAttributes = attributes.toMutableMap().apply {
            put("db_user_id", user.id.toString())
            put("email", email)
        }

        return DefaultOAuth2User(
            Collections.singleton(SimpleGrantedAuthority("ROLE_USER")),
            customAttributes,
            userNameAttributeName
        )
    }
}
