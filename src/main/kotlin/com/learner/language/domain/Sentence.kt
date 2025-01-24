package com.learner.language.domain

import jakarta.persistence.*

@Entity
@Table(name = "sentence")
class Sentence(

    @Column(name = "user_text", nullable = false)
    var userText: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
): BaseEntity()