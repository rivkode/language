package com.learner.language.domain.auth

data class AuthTokenSnapshot(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresInSeconds: Long,
)
