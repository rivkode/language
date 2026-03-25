package com.learner.language.domain.profile

import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.cliplearning.UserSavedClipRepository
import com.learner.language.infrastructure.profile.ProfileSavedClipQueryRepository
import com.learner.language.infrastructure.profile.ProfileSavedClipRow
import com.learner.language.infrastructure.profile.UserProfileRepository
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Component
class ProfileServiceImpl(
    private val userReader: UserReader,
    private val userProfileRepository: UserProfileRepository,
    private val userSavedClipRepository: UserSavedClipRepository,
    private val profileSavedClipQueryRepository: ProfileSavedClipQueryRepository
) : ProfileService {
    override fun retrieveProfile(viewerUserId: Long, profileUserId: Long): ProfileInfo {
        val user = userReader.getUserById(profileUserId)
        val profile = userProfileRepository.findByUserId(profileUserId).orElse(null)
        val savedClipCount = userSavedClipRepository.countByUserId(profileUserId)

        return ProfileInfo(
            userId = user.id,
            username = user.username,
            displayName = profile?.displayName ?: user.username,
            bio = profile?.bio,
            profileImageUrl = profile?.profileImageUrl,
            savedClipCount = savedClipCount,
            isMe = viewerUserId == profileUserId
        )
    }

    override fun retrieveSavedClips(profileUserId: Long, cursor: String?, size: Int, category: String?): SavedClipFeedInfo {
        userReader.getUserById(profileUserId)
        val rows = profileSavedClipQueryRepository.findSavedClipRows(
            userId = profileUserId,
            cursor = cursor,
            size = size + 1,
            category = category
        )
        val hasNext = rows.size > size
        val pageRows = if (hasNext) rows.take(size) else rows

        return SavedClipFeedInfo(
            items = pageRows.map { it.toSavedClipFeedItemInfo() },
            nextCursor = if (hasNext && pageRows.isNotEmpty()) pageRows.last().savedClipId.toString() else null,
            hasNext = hasNext
        )
    }

    override fun updateMyProfile(userId: Long, command: UpdateMyProfileCommand): ProfileInfo {
        val user = userReader.getUserById(userId)
        val profile = userProfileRepository.findByUserId(userId).orElse(null)
            ?: UserProfile(
                user = user,
                displayName = user.username
            )

        profile.displayName = command.displayName
        profile.bio = command.bio
        profile.profileImageUrl = command.profileImageUrl

        userProfileRepository.save(profile)

        return retrieveProfile(userId, userId)
    }

    private fun ProfileSavedClipRow.toSavedClipFeedItemInfo(): SavedClipFeedItemInfo {
        return SavedClipFeedItemInfo(
            savedClipId = savedClipId,
            savedAt = savedAt.toUtcString(),
            clip = SavedClipSummaryInfo(
                clipId = clipId,
                sourceVideoId = sourceVideoId,
                youtubeVideoId = youtubeVideoId,
                sourceUrl = sourceUrl,
                thumbnailUrl = thumbnailUrl,
                title = title,
                category = category,
                channelName = channelName,
                clipStartMs = clipStartMs,
                clipEndMs = clipEndMs,
                clipDurationMs = clipDurationMs,
                primarySentence = primarySentence
            )
        )
    }

    private fun java.time.LocalDateTime.toUtcString(): String {
        return this.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}
