package com.learner.language.application.auth

import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.user.UserRefreshTokenRepository
import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.LanguageException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthRefreshFacade(
    private val userRefreshTokenRepository: UserRefreshTokenRepository,
    private val userReader: UserReader,
    private val authTokenIssuer: AuthTokenIssuer,
) {

    @Transactional
    fun refresh(refreshToken: String): AuthTokenResult {
        if (refreshToken.isBlank()) {
            throw RefreshTokenInvalidException("refresh token이 비어있습니다.")
        }
        val stored = userRefreshTokenRepository.findByRefreshTokenRefreshToken(refreshToken)
            .orElseThrow { RefreshTokenInvalidException("유효하지 않은 refresh token 입니다.") }
        val user = userReader.getUserById(stored.userId)
        return authTokenIssuer.issue(user)
    }
}

class RefreshTokenInvalidException(message: String) : LanguageException(
    ErrorCode.REFRESH_TOKEN_INVALID,
    message,
    publicCode = ErrorCode.REFRESH_TOKEN_INVALID.name,
)
