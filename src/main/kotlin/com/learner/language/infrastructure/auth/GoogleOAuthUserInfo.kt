package com.learner.language.infrastructure.auth

import com.learner.language.application.auth.OAuthUserInfo
import com.learner.language.domain.user.AuthProvider

data class GoogleOAuthUserInfo(
    override val externalId: String,
    override val email: String,
    override val displayName: String,
) : OAuthUserInfo {
    override val provider: AuthProvider = AuthProvider.GOOGLE
}
