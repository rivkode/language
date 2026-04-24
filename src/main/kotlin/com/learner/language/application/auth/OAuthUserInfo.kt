package com.learner.language.application.auth

import com.learner.language.domain.user.AuthProvider

interface OAuthUserInfo {
    val provider: AuthProvider
    val externalId: String
    val email: String
    val displayName: String
}
