package com.learner.language.application.auth

import com.learner.language.domain.auth.AuthTokenSnapshot

data class AuthTokenResult(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresInSeconds: Long,
) {
    fun toSnapshot(): AuthTokenSnapshot = AuthTokenSnapshot(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        expiresInSeconds = expiresInSeconds,
    )

    companion object {
        fun from(snapshot: AuthTokenSnapshot): AuthTokenResult = AuthTokenResult(
            accessToken = snapshot.accessToken,
            refreshToken = snapshot.refreshToken,
            tokenType = snapshot.tokenType,
            expiresInSeconds = snapshot.expiresInSeconds,
        )
    }
}
