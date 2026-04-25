package com.learner.language.domain.diarychat

interface DiaryChatReader {
    fun getRoomById(roomId: Long): DiaryChatRoom
    fun findRoomByDiaryId(diaryId: Long): DiaryChatRoom?
    fun findParticipants(roomId: Long): List<DiaryChatParticipant>
    fun isParticipant(roomId: Long, userId: Long): Boolean
    fun findMessagesBefore(roomId: Long, before: Long?, size: Int): List<DiaryChatMessage>
    fun findMessagesAfter(roomId: Long, after: Long): List<DiaryChatMessage>
    fun findLastMessageId(roomId: Long): Long
    fun findMessageById(messageId: Long): DiaryChatMessage?
    fun findMessageCreatedAtById(messageId: Long): java.time.LocalDateTime?
    fun findRecentNonSystemMessages(roomId: Long, limit: Int): List<DiaryChatMessage>
}
