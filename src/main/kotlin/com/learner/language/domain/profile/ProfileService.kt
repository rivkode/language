package com.learner.language.domain.profile

interface ProfileService {
    fun retrieveProfile(viewerUserId: Long, profileUserId: Long): ProfileInfo
    fun retrieveSavedClips(profileUserId: Long, cursor: String?, size: Int, category: String?): SavedClipFeedInfo
    fun updateMyProfile(userId: Long, command: UpdateMyProfileCommand): ProfileInfo
}
