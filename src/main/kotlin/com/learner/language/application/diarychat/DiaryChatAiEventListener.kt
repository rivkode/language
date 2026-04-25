package com.learner.language.application.diarychat

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class DiaryChatAiEventListener(
    private val aiService: DiaryChatAiService,
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onRoomCreated(event: DiaryChatRoomCreatedEvent) {
        aiService.sendWelcome(event.roomId)
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onUserMessagePosted(event: DiaryChatUserMessagePostedEvent) {
        aiService.respondToUserMessage(event.roomId, event.messageId)
    }
}
