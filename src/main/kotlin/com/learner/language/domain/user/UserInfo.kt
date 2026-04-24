package com.learner.language.domain.user

import java.time.Instant
import java.time.ZoneOffset

data class UserInfo(
    val id: Long,
    val username: String,
    val email: String,
    val provider: String,
    val avatarUrl: String?,
    val createdAt: Instant,
) {
    companion object {
        fun of(user: User, avatarUrl: String? = null): UserInfo = UserInfo(
            id = user.id,
            username = user.username,
            email = user.email.email,
            provider = user.provider.name.lowercase(),
            avatarUrl = avatarUrl,
            createdAt = user.createdAt.toInstant(ZoneOffset.UTC),
        )
    }
}
