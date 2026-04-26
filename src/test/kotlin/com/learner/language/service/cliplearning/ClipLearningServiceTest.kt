package com.learner.language.service.cliplearning

import com.learner.language.application.cliplearning.ClipLearningServiceImpl
import com.learner.language.domain.cliplearning.ClipLearningTranscriptInfo
import com.learner.language.domain.cliplearning.ClipLearningTranscriptItemInfo
import com.learner.language.domain.cliplearning.ClipLearningTranscriptReader
import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.cliplearning.ClipLearningClipRepository
import com.learner.language.infrastructure.cliplearning.ClipLearningFeedQueryRepository
import com.learner.language.infrastructure.cliplearning.UserClipLearningProgressRepository
import com.learner.language.infrastructure.cliplearning.UserSavedClipRepository
import com.learner.language.interfaces.cliplearning.ClipLearningTranscriptDto
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ClipLearningServiceTest : BehaviorSpec({
    val clipLearningFeedQueryRepository = mockk<ClipLearningFeedQueryRepository>()
    val clipLearningClipRepository = mockk<ClipLearningClipRepository>()
    val userSavedClipRepository = mockk<UserSavedClipRepository>()
    val userClipLearningProgressRepository = mockk<UserClipLearningProgressRepository>()
    val userReader = mockk<UserReader>()
    val clipLearningTranscriptReader = mockk<ClipLearningTranscriptReader>()

    val clipLearningService = ClipLearningServiceImpl(
        clipLearningFeedQueryRepository = clipLearningFeedQueryRepository,
        clipLearningClipRepository = clipLearningClipRepository,
        userSavedClipRepository = userSavedClipRepository,
        userClipLearningProgressRepository = userClipLearningProgressRepository,
        userReader = userReader,
        clipLearningTranscriptReader = clipLearningTranscriptReader
    )

    afterTest {
        clearMocks(
            clipLearningFeedQueryRepository,
            clipLearningClipRepository,
            userSavedClipRepository,
            userClipLearningProgressRepository,
            userReader,
            clipLearningTranscriptReader
        )
    }

    Given("retrieveTranscript 호출 시") {
        val request = ClipLearningTranscriptDto.TranscriptRequest(youtubeVideoId = "Kkx6-9AJTY0")
        val transcriptInfo = ClipLearningTranscriptInfo(
            videoId = "Kkx6-9AJTY0",
            languagePriority = listOf("ko", "en"),
            count = 2,
            items = listOf(
                ClipLearningTranscriptItemInfo(
                    text = "첫 번째 문장",
                    start = 7.632,
                    duration = 5.031
                ),
                ClipLearningTranscriptItemInfo(
                    text = "두 번째 문장",
                    start = 12.663,
                    duration = 4.127
                )
            )
        )

        When("chatbot-service가 transcript를 반환하면") {
            every { clipLearningTranscriptReader.retrieveTranscript(request.youtubeVideoId) } returns transcriptInfo

            val response = clipLearningService.retrieveTranscript(request)

            Then("응답 DTO로 매핑해서 반환해야 한다") {
                response.youtubeVideoId shouldBe transcriptInfo.videoId
                response.languagePriority shouldBe transcriptInfo.languagePriority
                response.count shouldBe transcriptInfo.count
                response.items.first().text shouldBe "첫 번째 문장"
                verify(exactly = 1) { clipLearningTranscriptReader.retrieveTranscript(request.youtubeVideoId) }
            }
        }
    }
})
