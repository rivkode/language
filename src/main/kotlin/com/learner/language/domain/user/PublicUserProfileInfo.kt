package com.learner.language.domain.user

import java.time.Instant

data class PublicUserProfileInfo(
    val id: Long,
    val userId: Long,
    val username: String,
    val avatarUrl: String?,
    val provider: String,
    val createdAt: Instant,
    val diaryCount: Long,
    val followerCount: Long,
    val followingCount: Long,
    val isFollowing: Boolean,
    val bio: String?,
)
