package com.learner.language.infrastructure.auth

import com.learner.language.application.auth.OAuthUserInfo
import com.learner.language.domain.auth.OAuthAuthenticationException
import com.learner.language.domain.user.AuthProvider
import com.learner.language.system.exception.ErrorCode
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class NaverOAuthProviderClient(
    properties: OAuthProperties,
    @Qualifier("oauthRestClient") restClient: RestClient,
) : AbstractOAuthProviderClient(properties.naver, restClient) {

    override fun supports(): AuthProvider = AuthProvider.NAVER

    @Suppress("UNCHECKED_CAST")
    override fun mapToUserInfo(rawUserInfo: Map<String, Any?>): OAuthUserInfo {
        val response = rawUserInfo["response"] as? Map<String, Any?>
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "Naver userinfo missing response",
            )

        val externalId = response["id"] as? String
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "Naver userinfo missing id",
            )

        val email = response["email"] as? String
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_EMAIL_REQUIRED,
                "Naver 계정에서 email 동의가 필요합니다.",
            )

        val displayName = (response["name"] as? String)
            ?: (response["nickname"] as? String)
            ?: email.substringBefore('@')

        return NaverOAuthUserInfo(
            externalId = externalId,
            email = email,
            displayName = displayName,
        )
    }
}
