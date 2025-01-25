package com.learner.language.domain.feedback

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "feedback")
class Feedback(

    @Column(name = "ai_text", nullable = false)
    var aiText: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
) : BaseEntity()
