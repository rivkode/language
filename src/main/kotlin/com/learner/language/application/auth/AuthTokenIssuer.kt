package com.learner.language.application.auth

import com.learner.language.domain.user.User

interface AuthTokenIssuer {
    fun issue(user: User): AuthTokenResult
    fun revokeRefreshToken(userId: Long)
}
