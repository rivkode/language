package com.learner.language.domain.feedback

import com.learner.language.common.BaseEntity
import com.learner.language.domain.sentence.Sentence
import com.learner.language.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "feedback")
class Feedback(

    @Column(name = "ai_feedback", length = 3000 ,nullable = false)
    var aiFeedback: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sentence_id")
    val sentence: Sentence,
) : BaseEntity()
