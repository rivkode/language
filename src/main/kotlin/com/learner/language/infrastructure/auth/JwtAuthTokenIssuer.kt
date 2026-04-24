package com.learner.language.infrastructure.auth

import com.learner.language.application.auth.AuthTokenIssuer
import com.learner.language.application.auth.AuthTokenResult
import com.learner.language.domain.user.User
import com.learner.language.domain.user.UserRefreshToken
import com.learner.language.infrastructure.user.UserRefreshTokenRepository
import com.learner.language.system.security.JwtUtil
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class JwtAuthTokenIssuer(
    private val jwtUtil: JwtUtil,
    private val userRefreshTokenRepository: UserRefreshTokenRepository,
) : AuthTokenIssuer {

    override fun issue(user: User): AuthTokenResult {
        val rawToken = jwtUtil.createToken(user.id, user.email.email)
        val accessToken = if (rawToken.startsWith(JwtUtil.BEARER_PREFIX)) {
            rawToken.removePrefix(JwtUtil.BEARER_PREFIX)
        } else {
            rawToken
        }

        val refreshTokenEntity = upsertRefreshToken(user.id)

        return AuthTokenResult(
            accessToken = accessToken,
            refreshToken = refreshTokenEntity.refreshToken.refreshToken,
            tokenType = "Bearer",
            expiresInSeconds = ACCESS_TOKEN_TTL.seconds,
        )
    }

    override fun revokeRefreshToken(userId: Long) {
        userRefreshTokenRepository.findByUserId(userId).ifPresent {
            userRefreshTokenRepository.delete(it)
        }
    }

    private fun upsertRefreshToken(userId: Long): UserRefreshToken {
        val existing = userRefreshTokenRepository.findByUserId(userId)
        return if (existing.isPresent) {
            existing.get().apply {
                updateRefreshToken()
                userRefreshTokenRepository.save(this)
            }
        } else {
            userRefreshTokenRepository.save(UserRefreshToken(userId))
        }
    }

    companion object {
        private val ACCESS_TOKEN_TTL: Duration = Duration.ofDays(5)
    }
}
