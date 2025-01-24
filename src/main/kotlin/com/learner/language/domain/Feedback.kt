package com.learner.language.domain

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