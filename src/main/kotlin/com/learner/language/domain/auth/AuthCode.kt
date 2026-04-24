package com.learner.language.domain.auth

import java.time.Instant

data class AuthCode(
    val value: String,
    val snapshot: AuthTokenSnapshot,
    val expiresAt: Instant,
) {
    fun isExpired(now: Instant): Boolean = now.isAfter(expiresAt)
}
