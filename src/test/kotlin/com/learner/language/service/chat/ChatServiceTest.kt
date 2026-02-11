package com.learner.language.service.chat

import com.learner.language.domain.ai.AiAudioService
import com.learner.language.domain.ai.AiChatService
import com.learner.language.domain.chat.*
import com.learner.language.domain.prompt.PersonaType
import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.audio.AudioSpeechRepository
import com.learner.language.infrastructure.audio.AudioTranscribeRepository
import com.learner.language.infrastructure.chat.ChatAudioSpeechMatchRepository
import com.learner.language.infrastructure.chat.ChatRoomRepository
import com.learner.language.system.security.CustomPasswordEncoder
import com.learner.language.testutils.fixture.ChatFixture
import com.learner.language.testutils.fixture.UserFixture
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*


import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks

class ChatServiceTest : BehaviorSpec({

    // 1. Mock 객체 생성
    val chatWriter = mockk<ChatWriter>()
    val chatReader = mockk<ChatReader>()
    val chatRoomRepository = mockk<ChatRoomRepository>()
    val userReader = mockk<UserReader>()
    val aiChatService = mockk<AiChatService>()
    val aiAudioService = mockk<AiAudioService>()
    val audioTranscribeRepository = mockk<AudioTranscribeRepository>()
    val audioSpeechRepository = mockk<AudioSpeechRepository>()
    val chatAudioSpeechMatchRepository = mockk<ChatAudioSpeechMatchRepository>()
    val passwordEncoder = mockk<CustomPasswordEncoder>()

    // 테스트 대상 클래스 생성
    val chatServiceImpl = ChatServiceImpl(
        chatWriter, chatReader, chatRoomRepository, userReader,
        aiChatService, aiAudioService, audioTranscribeRepository,
        audioSpeechRepository, chatAudioSpeechMatchRepository
    )

    // 테스트가 끝날 때마다 Mock 초기화 (권장)
    afterTest {
        clearMocks(chatWriter, chatReader, chatRoomRepository, userReader, aiChatService)
    }

    Given("AI 채팅 시작(greetingChat) 시나리오에서") {
        val userId = 1L
        val chatRoomId = 100L
        val chatId = 1L
        val personaType = PersonaType.CHILD

        // Mocking 대신 실제 데이터에 가까운 객체 사용 (Fixture)
        every { passwordEncoder.encodePassword(any()) } returns "123456789"
        val user = UserFixture.createUser(passwordEncoder = passwordEncoder)
        val chatRoom = ChatFixture.createChatRoom(chatRoomId, user, personaType)
        val aiChatMessage = ChatFixture.createChatMessage(chatId, user, chatRoom, SenderType.AI)
        val command = ChatCommand.Generate(chatRoomId, personaType)

        When("사용자 정보와 채팅방이 존재하면") {
            // Stubbing: 'every'를 사용하여 동작 정의
            every { userReader.getUserById(userId) } returns user
            every { chatRoomRepository.findByUserIdAndPersonaType(userId, personaType.type) } returns chatRoom
            every { chatRoomRepository.findById(chatRoomId) } returns Optional.of(chatRoom)
            every { chatReader.getChatMessageListByChatRoomId(chatRoomId) } returns emptyList()
            every { chatReader.getLastChatMessageByChatRoomId(chatRoomId) } returns null
            every {
                aiChatService.greetingChat(eq(personaType), eq(user), eq(chatRoom), any(), any())
            } returns aiChatMessage
            every { chatWriter.save(any()) } returns aiChatMessage

            val result = chatServiceImpl.greetingChat(userId, command)

            Then("AI가 생성한 메시지 정보를 반환해야 한다") {
                result.message shouldBe "Test Message"
                // Verify: 실제 호출 여부 확인
                verify(exactly = 1) { aiChatService.greetingChat(any(), any(), any(), any(), any()) }
                verify(exactly = 1) { chatWriter.save(any()) }
            }
        }
    }

    Given("generateChat 메서드는") {
        val userId = 1L
        val chatRoomId = 100L
        val chatId = 1L
        val personaType = PersonaType.CHILD

        // 1. 실제 객체(Fixture) 준비 (Mock 대신 실제 객체 사용으로 에러 방지)
        every { passwordEncoder.encodePassword(any()) } returns "123456789"
        val user = UserFixture.createUser(passwordEncoder = passwordEncoder)
        val chatRoom = ChatFixture.createChatRoom(id = chatRoomId, user = user, personaType = personaType)

        val command = ChatCommand.Generate(chatRoomId = chatRoomId, personaType = personaType)

        // 2. 결과로 반환될 AI 메시지 준비
        val aiChatMessage = ChatFixture.createChatMessage(chatId, user, chatRoom, SenderType.AI)

        When("사용자가 질문을 던지면") {
            // 3. Stubbing (동작 정의)
            every { userReader.getUserById(userId) } returns user
            every { chatRoomRepository.findById(chatRoomId) } returns Optional.of(chatRoom)
            every { chatReader.getChatMessageListByChatRoomId(chatRoomId) } returns emptyList() // 이전 대화 기록
            every { chatReader.getLastChatMessageByChatRoomId(chatRoomId) } returns null // getNextSequence 내부용

            // aiChatService.generateChat 호출 시 aiChatMessage 반환하도록 설정
            every {
                aiChatService.generateChat(eq(command), eq(user), eq(chatRoom), any(), any())
            } returns aiChatMessage
            every { chatWriter.save(any()) } returns aiChatMessage

            // 4. 실행
            val result = chatServiceImpl.generateChat(command, userId)

            Then("AI가 생성한 답변 정보를 반환하고 DB에 저장해야 한다") {
                result.message shouldBe "Test Message"

                // 5. 검증 (중요한 호출이 실제로 일어났는지 확인)
                verify(exactly = 1) {
                    aiChatService.generateChat(any(), any(), any(), any(), any())
                }
                verify(exactly = 1) { chatWriter.save(any()) }
            }
        }
    }

})
