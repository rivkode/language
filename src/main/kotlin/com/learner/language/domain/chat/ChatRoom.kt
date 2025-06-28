package com.learner.language.domain.chat

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import jakarta.persistence.Column
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
    val user: User,

    @Column(name = "name")
    val name: String,

    @Column(name = "last_message_date_time")
    var lastMessageDateTime: LocalDateTime = LocalDateTime.now()

): BaseEntity() {
    fun updateLastMessageDateTime() {
        this.lastMessageDateTime = LocalDateTime.now()
    }
}