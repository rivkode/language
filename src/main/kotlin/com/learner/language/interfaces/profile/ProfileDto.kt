package com.learner.language.interfaces.profile

import com.learner.language.domain.profile.ProfileInfo
import com.learner.language.domain.profile.SavedClipFeedInfo
import com.learner.language.domain.profile.SavedClipFeedItemInfo
import com.learner.language.domain.profile.SavedClipSummaryInfo
import com.learner.language.domain.profile.UpdateMyProfileCommand
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class ProfileDto {
    data class ProfileResponse(
        val userId: Long,
        val username: String,
        val displayName: String,
        val bio: String?,
        val profileImageUrl: String?,
        val savedClipCount: Long,
        val isMe: Boolean,
    ) {
        constructor(profileInfo: ProfileInfo) : this(
            userId = profileInfo.userId,
            username = profileInfo.username,
            displayName = profileInfo.displayName,
            bio = profileInfo.bio,
            profileImageUrl = profileInfo.profileImageUrl,
            savedClipCount = profileInfo.savedClipCount,
            isMe = profileInfo.isMe
        )
    }

    data class UpdateMyProfileRequest(
        @field:NotBlank(message = "displayName은 필수 입력값입니다.")
        @field:Size(max = 30, message = "displayName은 30자 이하여야 합니다.")
        val displayName: String,
        @field:Size(max = 160, message = "bio는 160자 이하여야 합니다.")
        val bio: String?,
        val profileImageUrl: String?,
    ) {
        fun toCommand(): UpdateMyProfileCommand {
            return UpdateMyProfileCommand(
                displayName = displayName,
                bio = bio,
                profileImageUrl = profileImageUrl
            )
        }
    }
}

class ProfileSavedClipDto {
    data class FeedRequest(
        val cursor: String?,
        @field:Min(1, message = "size는 1 이상이어야 합니다.")
        @field:Max(30, message = "size는 30 이하여야 합니다.")
        val size: Int = 12,
        val category: String?,
    )

    data class FeedResponse(
        val items: List<SavedClipFeedItemResponse>,
        val paging: PagingResponse,
    ) {
        constructor(feedInfo: SavedClipFeedInfo) : this(
            items = feedInfo.items.map { SavedClipFeedItemResponse(it) },
            paging = PagingResponse(
                nextCursor = feedInfo.nextCursor,
                hasNext = feedInfo.hasNext
            )
        )
    }

    data class SavedClipFeedItemResponse(
        val savedClipId: Long,
        val savedAt: String,
        val clip: SavedClipSummaryResponse,
    ) {
        constructor(itemInfo: SavedClipFeedItemInfo) : this(
            savedClipId = itemInfo.savedClipId,
            savedAt = itemInfo.savedAt,
            clip = SavedClipSummaryResponse(itemInfo.clip)
        )
    }

    data class SavedClipSummaryResponse(
        val clipId: Long,
        val sourceVideoId: Long,
        val youtubeVideoId: String,
        val sourceUrl: String,
        val thumbnailUrl: String?,
        val title: String,
        val category: String,
        val channelName: String,
        val clipStartMs: Long,
        val clipEndMs: Long,
        val clipDurationMs: Long?,
        val primarySentence: String,
    ) {
        constructor(summaryInfo: SavedClipSummaryInfo) : this(
            clipId = summaryInfo.clipId,
            sourceVideoId = summaryInfo.sourceVideoId,
            youtubeVideoId = summaryInfo.youtubeVideoId,
            sourceUrl = summaryInfo.sourceUrl,
            thumbnailUrl = summaryInfo.thumbnailUrl,
            title = summaryInfo.title,
            category = summaryInfo.category,
            channelName = summaryInfo.channelName,
            clipStartMs = summaryInfo.clipStartMs,
            clipEndMs = summaryInfo.clipEndMs,
            clipDurationMs = summaryInfo.clipDurationMs,
            primarySentence = summaryInfo.primarySentence
        )
    }

    data class PagingResponse(
        val nextCursor: String?,
        val hasNext: Boolean,
    )
}
