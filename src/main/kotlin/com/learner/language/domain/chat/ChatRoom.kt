package com.learner.language.domain.chat

import com.learner.language.common.BaseEntity
import com.learner.language.domain.prompt.PersonaType
import com.learner.language.domain.prompt.PersonaTypeConverter
import com.learner.language.domain.user.User
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "chat_room")
class ChatRoom(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    @Column(name = "name")
    val name: String,

    @Convert(converter = PersonaTypeConverter::class)
    @Column
    var personaType: PersonaType,

    @Convert(converter = ChatContextTypeConverter::class)
    @Column(name = "context_type", nullable = false)
    var contextType: ChatContextType = ChatContextType.GENERAL,

    @Column(name = "video_id")
    var videoId: String? = null,

    @Column(name = "last_message_date_time")
    var lastMessageDateTime: LocalDateTime = LocalDateTime.now()

): BaseEntity() {
    fun updateLastMessageDateTime() {
        this.lastMessageDateTime = LocalDateTime.now()
    }

    constructor(
        user: User,
        personaType: PersonaType,
        contextType: ChatContextType = ChatContextType.GENERAL,
        videoId: String? = null
    ) : this(user, "name", personaType, contextType, videoId, LocalDateTime.now()) {
    }
}
