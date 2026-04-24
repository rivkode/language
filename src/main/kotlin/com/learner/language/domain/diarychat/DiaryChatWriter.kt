package com.learner.language.domain.diarychat

interface DiaryChatWriter {
    fun saveRoom(room: DiaryChatRoom): DiaryChatRoom
    fun saveParticipant(participant: DiaryChatParticipant): DiaryChatParticipant
    fun removeParticipant(roomId: Long, userId: Long): Int
    fun saveMessage(message: DiaryChatMessage): DiaryChatMessage
    fun appendEvent(
        roomId: Long,
        eventType: DiaryChatEventType,
        payloadJson: String?,
    ): DiaryChatMessage
    fun incrementParticipantCount(roomId: Long)
    fun decrementParticipantCount(roomId: Long)
}
