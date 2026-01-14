package com.learner.language.domain.user.auth

data class JwtAuthentication(
    val userId: Long, val accessToken: String
)
