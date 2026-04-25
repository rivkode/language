package com.learner.language.application.diarychat

data class CreateChatRoomCommand(
    val diaryId: Long,
    val aiAssistantEnabled: Boolean,
    val requesterUserId: Long,
)

data class SendMessageCommand(
    val roomId: Long,
    val authorUserId: Long,
    val text: String,
    val audioUrl: String?,
)
