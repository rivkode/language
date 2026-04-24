package com.learner.language.domain.diarychat

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "diary_chat_message")
class DiaryChatMessage(
    @Column(name = "room_id", nullable = false)
    var roomId: Long,

    @Column(name = "author_user_id")
    var authorUserId: Long?,

    @Column(name = "text", nullable = false, length = DiaryChatRoom.MESSAGE_MAX_LENGTH)
    var text: String,

    @Column(name = "audio_url", length = 1000)
    var audioUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 10)
    var source: DiaryChatMessageSource,

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 30)
    var eventType: DiaryChatEventType? = null,
) : BaseEntity() {

    fun isEvent(): Boolean = source == DiaryChatMessageSource.SYSTEM
}
