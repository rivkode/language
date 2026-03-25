package com.learner.language.application.profile

import com.learner.language.domain.profile.ProfileInfo
import com.learner.language.domain.profile.ProfileService
import com.learner.language.domain.profile.SavedClipFeedInfo
import com.learner.language.domain.profile.UpdateMyProfileCommand
import org.springframework.stereotype.Service

@Service
class ProfileFacade(
    private val profileService: ProfileService
) {
    fun retrieveProfile(viewerUserId: Long, profileUserId: Long): ProfileInfo {
        return profileService.retrieveProfile(viewerUserId, profileUserId)
    }

    fun retrieveMyProfile(userId: Long): ProfileInfo {
        return profileService.retrieveProfile(userId, userId)
    }

    fun retrieveSavedClips(profileUserId: Long, cursor: String?, size: Int, category: String?): SavedClipFeedInfo {
        return profileService.retrieveSavedClips(profileUserId, cursor, size, category)
    }

    fun updateMyProfile(userId: Long, command: UpdateMyProfileCommand): ProfileInfo {
        return profileService.updateMyProfile(userId, command)
    }
}
