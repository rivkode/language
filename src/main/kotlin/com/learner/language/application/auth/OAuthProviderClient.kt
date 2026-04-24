package com.learner.language.application.auth

import com.learner.language.domain.user.AuthProvider

interface OAuthProviderClient {
    fun supports(): AuthProvider
    fun fetchUserInfo(authorizationCode: String): OAuthUserInfo
}
