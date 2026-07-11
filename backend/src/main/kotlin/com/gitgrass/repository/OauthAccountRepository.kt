package com.gitgrass.repository

import com.gitgrass.domain.OauthAccount
import com.gitgrass.domain.OauthProvider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OauthAccountRepository : JpaRepository<OauthAccount, Long> {
    fun findByProviderAndProviderUserId(provider: OauthProvider, providerUserId: String): OauthAccount?
}
