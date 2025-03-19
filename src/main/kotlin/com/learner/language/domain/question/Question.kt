package com.learner.language.domain.question

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "question")
class Question(
    @Column(name = "user_question", nullable = false)
    var userQuestion: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User
): BaseEntity()
