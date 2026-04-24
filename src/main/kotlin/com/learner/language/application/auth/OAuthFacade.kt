package com.learner.language.application.auth

import com.learner.language.domain.auth.OAuthAuthenticationException
import com.learner.language.domain.user.AuthProvider
import com.learner.language.domain.user.User
import com.learner.language.domain.user.UserReader
import com.learner.language.domain.user.UserService
import com.learner.language.domain.user.UserWriter
import com.learner.language.system.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OAuthFacade(
    providerClients: List<OAuthProviderClient>,
    private val userReader: UserReader,
    private val userWriter: UserWriter,
    private val userService: UserService,
    private val authTokenIssuer: AuthTokenIssuer,
    private val authCodeStore: AuthCodeStore,
) {
    private val providerClientsByProvider: Map<AuthProvider, OAuthProviderClient> =
        providerClients.associateBy { it.supports() }

    @Transactional
    fun login(command: OAuthLoginCommand): String {
        val client = providerClientsByProvider[command.provider]
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_PROVIDER_ERROR,
                "지원하지 않는 OAuth provider 입니다: ${command.provider}",
            )

        val userInfo = client.fetchUserInfo(command.authorizationCode)
        val user = resolveUser(userInfo)
        val tokenResult = authTokenIssuer.issue(user)

        return authCodeStore.issue(tokenResult.toSnapshot())
    }

    private fun resolveUser(userInfo: OAuthUserInfo): User {
        val existing = userReader.findByProvider(userInfo.provider, userInfo.externalId)
        if (existing != null) {
            existing.updateUsername(userInfo.displayName)
            return userWriter.update(existing)
        }

        val byEmail = userReader.findByEmail(userInfo.email)
        if (byEmail != null) {
            throw OAuthAuthenticationException(
                ErrorCode.OAUTH_EMAIL_CONFLICT,
                "이미 존재하는 이메일입니다: ${userInfo.email}",
            )
        }

        return userService.createOAuthUser(
            email = userInfo.email,
            username = userInfo.displayName,
            provider = userInfo.provider,
            providerExternalId = userInfo.externalId,
        )
    }
}
