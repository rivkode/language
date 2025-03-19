package com.learner.language.domain.sentence

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "sentence")
class Sentence(

    @Column(name = "user_sentence", nullable = false)
    var userSentence: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
): BaseEntity()
