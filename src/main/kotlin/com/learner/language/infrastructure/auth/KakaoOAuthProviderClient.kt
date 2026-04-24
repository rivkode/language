package com.learner.language.infrastructure.auth

import com.learner.language.application.auth.OAuthUserInfo
import com.learner.language.domain.auth.OAuthAuthenticationException
import com.learner.language.domain.user.AuthProvider
import com.learner.language.system.exception.ErrorCode
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class KakaoOAuthProviderClient(
    properties: OAuthProperties,
    @Qualifier("oauthRestClient") restClient: RestClient,
) : AbstractOAuthProviderClient(properties.kakao, restClient) {

    override fun supports(): AuthProvider = AuthProvider.KAKAO

    @Suppress("UNCHECKED_CAST")
    override fun mapToUserInfo(rawUserInfo: Map<String, Any?>): OAuthUserInfo {
        val externalId = (rawUserInfo["id"] as? Number)?.toString()
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "Kakao userinfo missing id",
            )

        val account = rawUserInfo["kakao_account"] as? Map<String, Any?>
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "Kakao userinfo missing kakao_account",
            )

        val email = account["email"] as? String
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_EMAIL_REQUIRED,
                "Kakao 계정에서 email 동의가 필요합니다.",
            )

        val profile = account["profile"] as? Map<String, Any?>
        val displayName = (profile?.get("nickname") as? String)
            ?: email.substringBefore('@')

        return KakaoOAuthUserInfo(
            externalId = externalId,
            email = email,
            displayName = displayName,
        )
    }
}
