package com.learner.language.domain

import jakarta.persistence.*

@Entity
@Table(name = "word")
class Word(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "label", nullable = false)
    var label: String,

    @Column(name = "description")
    var description: String,
): BaseEntity() {
}
