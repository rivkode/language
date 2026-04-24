package com.learner.language.application.auth

import com.learner.language.domain.user.AuthProvider

data class OAuthLoginCommand(
    val provider: AuthProvider,
    val authorizationCode: String,
)

data class AuthExchangeCommand(
    val code: String,
)
