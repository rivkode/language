package com.learner.language.domain.chat.answer

import com.learner.language.common.BaseEntity
import com.learner.language.domain.chat.question.Question
import com.learner.language.domain.user.User
import jakarta.persistence.*

@Entity
@Table( name = "answer")
class Answer(

    @Column(name = "ai_answer", length = 3000, nullable = false)
    var aiAnswer: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    val question: Question
): BaseEntity()
