package com.learner.language.domain

import jakarta.persistence.*

@Entity
@Table(name = "word")
class Word(

    @Column(name = "label", nullable = false)
    var label: String,

    @Column(name = "description")
    var description: String,
): BaseEntity()
