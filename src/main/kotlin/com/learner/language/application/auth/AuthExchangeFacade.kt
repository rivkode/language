package com.learner.language.application.auth

import com.learner.language.domain.auth.OAuthAuthenticationException
import com.learner.language.system.exception.ErrorCode
import org.springframework.stereotype.Service

@Service
class AuthExchangeFacade(
    private val authCodeStore: AuthCodeStore,
) {
    fun exchange(command: AuthExchangeCommand): AuthTokenResult {
        val snapshot = authCodeStore.consume(command.code)
            ?: throw OAuthAuthenticationException(
                ErrorCode.OAUTH_CODE_INVALID,
                "유효하지 않거나 만료된 authorization code 입니다.",
            )
        return AuthTokenResult.from(snapshot)
    }
}
