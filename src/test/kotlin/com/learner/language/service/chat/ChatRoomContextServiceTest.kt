package com.learner.language.service.chat

import com.learner.language.domain.ai.AiAudioService
import com.learner.language.domain.ai.AiChatService
import com.learner.language.domain.chat.ChatContextType
import com.learner.language.domain.chat.ChatRoomCommand
import com.learner.language.domain.chat.ChatServiceImpl
import com.learner.language.domain.cliplearning.ClipLearningTranscriptInfo
import com.learner.language.domain.cliplearning.ClipLearningTranscriptItemInfo
import com.learner.language.domain.cliplearning.ClipLearningTranscriptReader
import com.learner.language.domain.prompt.PersonaType
import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.audio.AudioSpeechRepository
import com.learner.language.infrastructure.audio.AudioTranscribeRepository
import com.learner.language.infrastructure.chat.ChatAudioSpeechMatchRepository
import com.learner.language.infrastructure.chat.ChatRoomRepository
import com.learner.language.system.exception.BadRequestException
import com.learner.language.system.security.CustomPasswordEncoder
import com.learner.language.testutils.fixture.UserFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ChatRoomContextServiceTest : BehaviorSpec({
    val chatWriter = mockk<com.learner.language.domain.chat.ChatWriter>()
    val chatReader = mockk<com.learner.language.domain.chat.ChatReader>()
    val chatRoomRepository = mockk<ChatRoomRepository>()
    val userReader = mockk<UserReader>()
    val aiChatService = mockk<AiChatService>()
    val aiAudioService = mockk<AiAudioService>()
    val audioTranscribeRepository = mockk<AudioTranscribeRepository>()
    val audioSpeechRepository = mockk<AudioSpeechRepository>()
    val chatAudioSpeechMatchRepository = mockk<ChatAudioSpeechMatchRepository>()
    val clipLearningTranscriptReader = mockk<ClipLearningTranscriptReader>()
    val passwordEncoder = mockk<CustomPasswordEncoder>()

    val chatService = ChatServiceImpl(
        chatWriter = chatWriter,
        chatReader = chatReader,
        chatRoomRepository = chatRoomRepository,
        userReader = userReader,
        aiChatService = aiChatService,
        aiAudioService = aiAudioService,
        audioTranscribeRepository = audioTranscribeRepository,
        audioSpeechRepository = audioSpeechRepository,
        chatAudioSpeechMatchRepository = chatAudioSpeechMatchRepository,
        clipLearningTranscriptReader = clipLearningTranscriptReader
    )

    afterTest {
        clearMocks(
            chatWriter,
            chatReader,
            chatRoomRepository,
            userReader,
            aiChatService,
            aiAudioService,
            audioTranscribeRepository,
            audioSpeechRepository,
            chatAudioSpeechMatchRepository,
            clipLearningTranscriptReader
        )
    }

    Given("saveChatRoom 호출 시") {
        val userId = 1L
        every { passwordEncoder.encodePassword(any()) } returns "encoded-password"
        val user = UserFixture.createUser(passwordEncoder = passwordEncoder)

        When("VIDEO_TRANSCRIPT 타입인데 youtubeVideoId가 없으면") {
            every { userReader.getUserById(userId) } returns user
            every { chatRoomRepository.save(any()) } answers { firstArg() }
            val command = ChatRoomCommand.Register(
                personaType = PersonaType.TEACHER,
                contextType = ChatContextType.VIDEO_TRANSCRIPT,
                youtubeVideoId = null
            )

            Then("BadRequestException이 발생해야 한다") {
                shouldThrow<BadRequestException> {
                    chatService.saveChatRoom(userId, command)
                }
            }
        }

        When("GENERAL 타입인데 youtubeVideoId가 들어오면") {
            every { userReader.getUserById(userId) } returns user
            every { chatRoomRepository.save(any()) } answers { firstArg() }
            val command = ChatRoomCommand.Register(
                personaType = PersonaType.CHILD,
                contextType = ChatContextType.GENERAL,
                youtubeVideoId = "Kkx6-9AJTY0"
            )

            Then("BadRequestException이 발생해야 한다") {
                shouldThrow<BadRequestException> {
                    chatService.saveChatRoom(userId, command)
                }
            }
        }

        When("VIDEO_TRANSCRIPT 타입과 유효한 youtubeVideoId가 들어오면") {
            every { userReader.getUserById(userId) } returns user
            every { chatRoomRepository.save(any()) } answers { firstArg() }
            val command = ChatRoomCommand.Register(
                personaType = PersonaType.TEACHER,
                contextType = ChatContextType.VIDEO_TRANSCRIPT,
                youtubeVideoId = "Kkx6-9AJTY0",
                name = "카페 표현 연습"
            )
            every { clipLearningTranscriptReader.retrieveTranscript("Kkx6-9AJTY0") } returns ClipLearningTranscriptInfo(
                videoId = "Kkx6-9AJTY0",
                languagePriority = listOf("ko", "en"),
                count = 1,
                items = listOf(
                    ClipLearningTranscriptItemInfo(
                        text = "아이스 아메리카노 한 잔 주세요.",
                        start = 7.632,
                        duration = 5.031
                    )
                )
            )

            val result = chatService.saveChatRoom(userId, command)

            Then("transcript를 검증하고 youtubeVideoId를 가진 방을 생성해야 한다") {
                result.contextType shouldBe ChatContextType.VIDEO_TRANSCRIPT.name
                result.youtubeVideoId shouldBe "Kkx6-9AJTY0"
                verify(exactly = 1) { clipLearningTranscriptReader.retrieveTranscript("Kkx6-9AJTY0") }
                verify(exactly = 1) { chatRoomRepository.save(any()) }
            }
        }
    }
})
