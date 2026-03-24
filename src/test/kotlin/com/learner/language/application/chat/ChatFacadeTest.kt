package com.learner.language.application.chat

import com.learner.language.domain.chat.*
import com.learner.language.domain.prompt.PersonaType
import com.learner.language.domain.user.UserReader
import com.learner.language.system.security.CustomPasswordEncoder
import com.learner.language.testutils.fixture.ChatFixture
import com.learner.language.testutils.fixture.UserFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.test.util.ReflectionTestUtils

class ChatFacadeTest : BehaviorSpec({
    val chatService = mockk<ChatService>()
    val userReader = mockk<UserReader>()
    val chatRoomReader = mockk<ChatRoomReader>()
    val chatReader = mockk<ChatReader>()
    val chatWriter = mockk<ChatWriter>()
    val passwordEncoder = mockk<CustomPasswordEncoder>()

    val chatFacade = ChatFacade(
        chatService = chatService,
        userReader = userReader,
        chatRoomReader = chatRoomReader,
        chatReader = chatReader,
        chatWriter = chatWriter
    )

    afterTest {
        clearMocks(chatService, userReader, chatRoomReader, chatReader, chatWriter)
    }

    given("greetingChatV2를 호출하면") {
        val userId = 1L
        val chatRoomId = 10L
        val personaType = PersonaType.CHILD
        val command = ChatCommand.Generate(chatRoomId = chatRoomId, personaType = personaType)

        every { passwordEncoder.encodePassword(any()) } returns "encoded-password"
        val user = UserFixture.createUser(passwordEncoder = passwordEncoder).also {
            ReflectionTestUtils.setField(it, "id", userId)
        }
        val chatRoom = ChatFixture.createChatRoom(id = chatRoomId, user = user, personaType = personaType)
        val chatMessageList = listOf(
            ChatFixture.createChatMessage(id = 1L, user = user, chatRoom = chatRoom, senderType = SenderType.USER, sequence = 1)
        )
        val greetingChatMessage =
            ChatFixture.createChatMessage(id = 2L, user = user, chatRoom = chatRoom, senderType = SenderType.AI, sequence = 2)

        `when`("facade가 유스케이스를 조립하면") {
            every { userReader.getUserById(userId) } returns user
            every { chatRoomReader.getChatRoomById(chatRoomId) } returns chatRoom
            every { chatReader.getChatMessageListByChatRoomId(chatRoomId) } returns chatMessageList
            every {
                chatService.createGreetingChatMessage(
                    user = user,
                    chatRoom = chatRoom,
                    personaType = personaType,
                    chatMessageList = chatMessageList
                )
            } returns greetingChatMessage
            every { chatWriter.save(greetingChatMessage) } returns greetingChatMessage

            val result = chatFacade.greetingChatV2(userId, command)

            then("조회, 생성, 저장 흐름을 facade에서 순서대로 조합한다") {
                result.message shouldBe greetingChatMessage.message
                result.chatMessageId shouldBe greetingChatMessage.id

                verify(exactly = 1) { userReader.getUserById(userId) }
                verify(exactly = 1) { chatRoomReader.getChatRoomById(chatRoomId) }
                verify(exactly = 1) { chatReader.getChatMessageListByChatRoomId(chatRoomId) }
                verify(exactly = 1) {
                    chatService.createGreetingChatMessage(
                        user = user,
                        chatRoom = chatRoom,
                        personaType = personaType,
                        chatMessageList = chatMessageList
                    )
                }
                verify(exactly = 1) { chatWriter.save(greetingChatMessage) }
            }
        }
    }

    given("phraseChat을 호출하면") {
        val userId = 1L
        val chatRoomId = 20L
        val chatId = 5L
        val command = ChatCommand.Phrase(chatRoomId = chatRoomId, chatId = chatId, userId = userId)

        every { passwordEncoder.encodePassword(any()) } returns "encoded-password"
        val user = UserFixture.createUser(passwordEncoder = passwordEncoder).also {
            ReflectionTestUtils.setField(it, "id", userId)
        }
        val chatRoom = ChatFixture.createChatRoom(id = chatRoomId, user = user, personaType = PersonaType.CHILD)
        val previousChatMessages = listOf(
            ChatFixture.createChatMessage(id = 1L, user = user, chatRoom = chatRoom, senderType = SenderType.AI, sequence = 1),
            ChatFixture.createChatMessage(id = 2L, user = user, chatRoom = chatRoom, senderType = SenderType.USER, sequence = 2)
        )
        val currentChatMessage =
            ChatFixture.createChatMessage(id = chatId, user = user, chatRoom = chatRoom, senderType = SenderType.USER, sequence = 3)
        val phraseChatMessage =
            ChatFixture.createChatMessage(id = 6L, user = user, chatRoom = chatRoom, senderType = SenderType.AI, sequence = 4)

        `when`("facade가 phrase 유스케이스를 조립하면") {
            every { userReader.getUserById(userId) } returns user
            every { chatRoomReader.getChatRoomById(chatRoomId) } returns chatRoom
            every { chatReader.getChatMessageById(chatId) } returns currentChatMessage
            every { chatReader.getPreviousChatMessages(chatRoomId, currentChatMessage.sequence, 4) } returns previousChatMessages
            every {
                chatService.createPhraseChatMessage(
                    user = user,
                    chatRoom = chatRoom,
                    currentChatMessage = currentChatMessage,
                    previousChatMessages = previousChatMessages
                )
            } returns phraseChatMessage
            every { chatWriter.save(phraseChatMessage) } returns phraseChatMessage

            val result = chatFacade.phraseChat(command)

            then("현재 메시지와 직전 4개 히스토리를 조합해 응답을 저장한다") {
                result.message shouldBe phraseChatMessage.message
                result.chatMessageId shouldBe phraseChatMessage.id

                verify(exactly = 1) { userReader.getUserById(userId) }
                verify(exactly = 1) { chatRoomReader.getChatRoomById(chatRoomId) }
                verify(exactly = 1) { chatReader.getChatMessageById(chatId) }
                verify(exactly = 1) { chatReader.getPreviousChatMessages(chatRoomId, currentChatMessage.sequence, 4) }
                verify(exactly = 1) {
                    chatService.createPhraseChatMessage(
                        user = user,
                        chatRoom = chatRoom,
                        currentChatMessage = currentChatMessage,
                        previousChatMessages = previousChatMessages
                    )
                }
                verify(exactly = 1) { chatWriter.save(phraseChatMessage) }
            }
        }
    }
})
