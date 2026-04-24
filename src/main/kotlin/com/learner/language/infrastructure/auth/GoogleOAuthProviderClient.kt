package com.learner.language.infrastructure.auth

import com.learner.language.application.auth.OAuthUserInfo
import com.learner.language.domain.auth.OAuthAuthenticationException
import com.learner.language.domain.user.AuthProvider
import com.learner.language.system.exception.ErrorCode
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class GoogleOAuthProviderClient(
    properties: OAuthProperties,
    @Qualifier("oauthRestClient") restClient: RestClient,
) : AbstractOAuthProviderClient(properties.google, restClient) {

    override fun supports(): AuthProvider = AuthProvider.GOOGLE

    override fun mapToUserInfo(rawUserInfo: Map<String, Any?>): OAuthUserInfo {
        val externalId = rawUserInfo["sub"] as? String
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "Google userinfo missing sub",
            )

        val email = rawUserInfo["email"] as? String
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_EMAIL_REQUIRED,
                "Google 계정에서 email scope 동의가 필요합니다.",
            )

        val displayName = (rawUserInfo["name"] as? String)
            ?: (rawUserInfo["given_name"] as? String)
            ?: email.substringBefore('@')

        return GoogleOAuthUserInfo(
            externalId = externalId,
            email = email,
            displayName = displayName,
        )
    }
}
