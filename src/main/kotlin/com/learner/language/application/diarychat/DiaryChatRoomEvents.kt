package com.learner.language.application.diarychat

data class DiaryChatRoomCreatedEvent(val roomId: Long)

data class DiaryChatUserMessagePostedEvent(
    val roomId: Long,
    val messageId: Long,
)
