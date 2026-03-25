package com.learner.language.testutils.persistence

import com.learner.language.domain.cliplearning.ClipLearningClip
import com.learner.language.domain.cliplearning.ClipLearningSentence
import com.learner.language.domain.cliplearning.ClipSourceVideo
import com.learner.language.domain.cliplearning.UserSavedClip
import com.learner.language.domain.user.User
import com.learner.language.infrastructure.cliplearning.ClipLearningClipRepository
import com.learner.language.infrastructure.cliplearning.ClipLearningSentenceRepository
import com.learner.language.infrastructure.cliplearning.ClipSourceVideoRepository
import com.learner.language.infrastructure.cliplearning.UserSavedClipRepository

class ClipLearningPersistenceUtils(
    private val clipSourceVideoRepository: ClipSourceVideoRepository,
    private val clipLearningClipRepository: ClipLearningClipRepository,
    private val clipLearningSentenceRepository: ClipLearningSentenceRepository,
    private val userSavedClipRepository: UserSavedClipRepository
) {
    fun saveSourceVideo(
        youtubeVideoId: String,
        sourceUrl: String,
        sourceTitle: String,
        channelName: String,
        thumbnailUrl: String? = null
    ): ClipSourceVideo {
        return clipSourceVideoRepository.save(
            ClipSourceVideo(
                youtubeVideoId = youtubeVideoId,
                sourceUrl = sourceUrl,
                sourceTitle = sourceTitle,
                channelName = channelName,
                thumbnailUrl = thumbnailUrl
            )
        )
    }

    fun saveClip(
        sourceVideo: ClipSourceVideo,
        title: String,
        category: String,
        clipStartMs: Long,
        clipEndMs: Long,
        primarySentence: String
    ): ClipLearningClip {
        val clip = clipLearningClipRepository.save(
            ClipLearningClip(
                sourceVideo = sourceVideo,
                title = title,
                category = category,
                clipStartMs = clipStartMs,
                clipEndMs = clipEndMs,
                clipDurationMs = clipEndMs - clipStartMs
            )
        )
        clipLearningSentenceRepository.save(
            ClipLearningSentence(
                clip = clip,
                primarySentence = primarySentence,
                translation = null,
                explanationSummary = null,
                usageTip = null
            )
        )

        return clip
    }

    fun saveSavedClip(user: User, clip: ClipLearningClip): UserSavedClip {
        return userSavedClipRepository.save(
            UserSavedClip(
                user = user,
                clip = clip
            )
        )
    }
}
