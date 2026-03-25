package com.learner.language.application.cliplearning

import com.learner.language.domain.cliplearning.ClipLearningClip
import com.learner.language.domain.cliplearning.ClipLearningVocabulary
import com.learner.language.domain.cliplearning.ClipSourceVideo
import com.learner.language.domain.cliplearning.UserClipLearningProgress
import com.learner.language.domain.cliplearning.UserSavedClip
import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.cliplearning.ClipLearningClipRepository
import com.learner.language.infrastructure.cliplearning.ClipLearningFeedQueryRepository
import com.learner.language.infrastructure.cliplearning.ClipLearningFeedRow
import com.learner.language.infrastructure.cliplearning.ClipLearningVocabularyRepository
import com.learner.language.infrastructure.cliplearning.UserClipLearningProgressRepository
import com.learner.language.infrastructure.cliplearning.UserSavedClipRepository
import com.learner.language.interfaces.cliplearning.ClipLearningProgressDto
import com.learner.language.interfaces.cliplearning.ClipLearningSaveDto
import com.learner.language.testutils.fixture.UserFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.springframework.test.util.ReflectionTestUtils
import java.time.LocalDateTime
import java.util.Optional

class ClipLearningServiceImplTest : BehaviorSpec({
    val clipLearningFeedQueryRepository = mockk<ClipLearningFeedQueryRepository>()
    val clipLearningClipRepository = mockk<ClipLearningClipRepository>()
    val clipLearningVocabularyRepository = mockk<ClipLearningVocabularyRepository>()
    val userSavedClipRepository = mockk<UserSavedClipRepository>()
    val userClipLearningProgressRepository = mockk<UserClipLearningProgressRepository>()
    val userReader = mockk<UserReader>()

    val service = ClipLearningServiceImpl(
        clipLearningFeedQueryRepository = clipLearningFeedQueryRepository,
        clipLearningClipRepository = clipLearningClipRepository,
        clipLearningVocabularyRepository = clipLearningVocabularyRepository,
        userSavedClipRepository = userSavedClipRepository,
        userClipLearningProgressRepository = userClipLearningProgressRepository,
        userReader = userReader
    )

    afterTest {
        clearMocks(
            clipLearningFeedQueryRepository,
            clipLearningClipRepository,
            clipLearningVocabularyRepository,
            userSavedClipRepository,
            userClipLearningProgressRepository,
            userReader
        )
    }

    given("retrieveClip is called") {
        val userId = 1L
        val clipId = 1001L
        val row = clipRow(clipId = clipId)
        val vocabulary = clipVocabularyEntities(clipId)
        val progress = userClipLearningProgress(userId = userId, clipId = clipId, completed = true)

        `when`("saved and progress data exist") {
            every { clipLearningFeedQueryRepository.findClipRowByClipId(clipId) } returns row
            every { clipLearningVocabularyRepository.findAllByClipIdOrderByDisplayOrderAsc(clipId) } returns vocabulary
            every { userSavedClipRepository.findByUserIdAndClipId(userId, clipId) } returns Optional.of(userSavedClip(userId, clipId))
            every { userClipLearningProgressRepository.findByUserIdAndClipId(userId, clipId) } returns Optional.of(progress)

            val response = service.retrieveClip(userId, clipId)

            then("clip detail response is assembled from DB-backed data") {
                response.clipId shouldBe clipId
                response.youtubeVideoId shouldBe "youtube-video-id"
                response.userState.saved shouldBe true
                response.userState.completed shouldBe true
                response.explanation?.vocabulary?.size shouldBe 2
            }
        }
    }

    given("saveClip is called") {
        val userId = 1L
        val clipId = 1001L
        val user = user(userId)
        val clip = clip(clipId)

        `when`("saved=true is requested and no existing row exists") {
            val savedClipSlot = slot<UserSavedClip>()
            every { clipLearningClipRepository.findById(clipId) } returns Optional.of(clip)
            every { userSavedClipRepository.findByUserIdAndClipId(userId, clipId) } returns Optional.empty()
            every { userReader.getUserById(userId) } returns user
            every { userSavedClipRepository.save(capture(savedClipSlot)) } answers {
                savedClipSlot.captured.apply {
                    ReflectionTestUtils.setField(this, "createdAt", LocalDateTime.of(2026, 3, 21, 10, 20, 0))
                    ReflectionTestUtils.setField(this, "updatedAt", LocalDateTime.of(2026, 3, 21, 10, 20, 0))
                }
            }

            val response = service.saveClip(
                userId = userId,
                clipId = clipId,
                request = ClipLearningSaveDto.SaveRequest(saved = true)
            )

            then("it persists saved state and returns the saved timestamp") {
                response.clipId shouldBe clipId
                response.saved shouldBe true
                response.savedAt shouldBe "2026-03-21T10:20:00Z"

                verify(exactly = 1) { userSavedClipRepository.save(any()) }
            }
        }

        `when`("saved=false is requested") {
            every { clipLearningClipRepository.findById(clipId) } returns Optional.of(clip)
            every { userSavedClipRepository.deleteByUserIdAndClipId(userId, clipId) } just runs

            val response = service.saveClip(
                userId = userId,
                clipId = clipId,
                request = ClipLearningSaveDto.SaveRequest(saved = false)
            )

            then("it deletes saved state and returns null savedAt") {
                response.saved shouldBe false
                response.savedAt shouldBe null

                verify(exactly = 1) { userSavedClipRepository.deleteByUserIdAndClipId(userId, clipId) }
            }
        }
    }

    given("updateProgress is called") {
        val userId = 1L
        val clipId = 1002L
        val user = user(userId)
        val clip = clip(clipId)

        `when`("valid progress values are sent") {
            val progressSlot = slot<UserClipLearningProgress>()
            every { clipLearningClipRepository.findById(clipId) } returns Optional.of(clip)
            every { userClipLearningProgressRepository.findByUserIdAndClipId(userId, clipId) } returns Optional.empty()
            every { userReader.getUserById(userId) } returns user
            every { userClipLearningProgressRepository.save(capture(progressSlot)) } answers {
                progressSlot.captured.apply {
                    ReflectionTestUtils.setField(this, "updatedAt", LocalDateTime.of(2026, 3, 21, 10, 25, 0))
                }
            }

            val response = service.updateProgress(
                userId = userId,
                request = ClipLearningProgressDto.ProgressRequest(
                    clipId = clipId,
                    lastViewedPositionMs = 5400L,
                    repeatEnabled = true,
                    translationVisible = false,
                    completed = true
                )
            )

            then("it upserts progress state and returns updated timestamp") {
                response.clipId shouldBe clipId
                response.updatedAt shouldBe "2026-03-21T10:25:00Z"
                progressSlot.captured.completed shouldBe true
                progressSlot.captured.repeatEnabled shouldBe true
                progressSlot.captured.translationVisible shouldBe false
                progressSlot.captured.lastViewedPositionMs shouldBe 5400L
            }
        }
    }
}) {
    companion object {
        private fun clipRow(clipId: Long) = ClipLearningFeedRow(
            clipId = clipId,
            sourceVideoId = 501L,
            youtubeVideoId = "youtube-video-id",
            sourceUrl = "https://www.youtube.com/watch?v=youtube-video-id",
            title = "Ordering Coffee Naturally",
            category = "daily-conversation",
            channelName = "Korean Daily Clips",
            clipStartMs = 12_000L,
            clipEndMs = 21_500L,
            clipDurationMs = 9_500L,
            primarySentence = "아이스 아메리카노 한 잔 주세요.",
            translation = "I'd like one iced Americano, please.",
            explanationSummary = "Useful Korean expression for ordering.",
            usageTip = "Practice the clip in a loop.",
            thumbnailUrl = "https://img.youtube.com/vi/youtube-video-id/hqdefault.jpg"
        )

        private fun clip(clipId: Long): ClipLearningClip {
            val sourceVideo = ClipSourceVideo(
                youtubeVideoId = "youtube-video-id",
                sourceUrl = "https://www.youtube.com/watch?v=youtube-video-id",
                sourceTitle = "Source Video",
                channelName = "Korean Daily Clips",
                thumbnailUrl = "https://img.youtube.com/vi/youtube-video-id/hqdefault.jpg"
            ).also {
                ReflectionTestUtils.setField(it, "id", 501L)
            }

            return ClipLearningClip(
                sourceVideo = sourceVideo,
                title = "Ordering Coffee Naturally",
                category = "daily-conversation",
                clipStartMs = 12_000L,
                clipEndMs = 21_500L,
                clipDurationMs = 9_500L
            ).also {
                ReflectionTestUtils.setField(it, "id", clipId)
            }
        }

        private fun clipVocabularyEntities(clipId: Long): List<ClipLearningVocabulary> {
            val clip = clip(clipId)
            return listOf(
                ClipLearningVocabulary(clip = clip, word = "아이스", meaning = "iced", displayOrder = 1),
                ClipLearningVocabulary(clip = clip, word = "한 잔", meaning = "one cup", displayOrder = 2)
            )
        }

        private fun user(userId: Long) = UserFixture.createUser(passwordEncoder = mockk(relaxed = true)).also {
            ReflectionTestUtils.setField(it, "id", userId)
        }

        private fun userSavedClip(userId: Long, clipId: Long): UserSavedClip {
            val savedClip = UserSavedClip(
                user = user(userId),
                clip = clip(clipId)
            )
            ReflectionTestUtils.setField(savedClip, "createdAt", LocalDateTime.of(2026, 3, 21, 10, 20, 0))
            ReflectionTestUtils.setField(savedClip, "updatedAt", LocalDateTime.of(2026, 3, 21, 10, 20, 0))
            return savedClip
        }

        private fun userClipLearningProgress(userId: Long, clipId: Long, completed: Boolean): UserClipLearningProgress {
            val progress = UserClipLearningProgress(
                user = user(userId),
                clip = clip(clipId),
                lastViewedPositionMs = 5_400L,
                repeatEnabled = true,
                translationVisible = false,
                completed = completed
            )
            ReflectionTestUtils.setField(progress, "updatedAt", LocalDateTime.of(2026, 3, 21, 10, 25, 0))
            return progress
        }
    }
}
