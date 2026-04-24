package com.learner.language.application.auth

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthLogoutFacade(
    private val authTokenIssuer: AuthTokenIssuer,
) {
    @Transactional
    fun logout(userId: Long) {
        authTokenIssuer.revokeRefreshToken(userId)
    }
}
