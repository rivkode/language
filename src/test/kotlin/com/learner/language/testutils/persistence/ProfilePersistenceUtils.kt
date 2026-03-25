package com.learner.language.testutils.persistence

import com.learner.language.domain.profile.UserProfile
import com.learner.language.domain.user.User
import com.learner.language.infrastructure.profile.UserProfileRepository

class ProfilePersistenceUtils(
    private val userProfileRepository: UserProfileRepository
) {
    fun saveUserProfile(
        user: User,
        displayName: String = user.username,
        bio: String? = null,
        profileImageUrl: String? = null
    ): UserProfile {
        return userProfileRepository.save(
            UserProfile(
                user = user,
                displayName = displayName,
                bio = bio,
                profileImageUrl = profileImageUrl
            )
        )
    }
}
