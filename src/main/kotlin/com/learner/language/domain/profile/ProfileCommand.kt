package com.learner.language.domain.profile

data class UpdateMyProfileCommand(
    val displayName: String,
    val bio: String?,
    val profileImageUrl: String?
)
