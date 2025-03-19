package com.learner.language.domain.word

import com.learner.language.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "word")
class Word(

    @Column(name = "label", nullable = false)
    var label: String,

    @Column(name = "description")
    var description: String,

    @Convert(converter = PartConverter::class)
    @Column
    var part: Part,

    @Convert(converter = LevelConverter::class)
    @Column
    var level: CefrLevel,

): BaseEntity()

enum class Part(
    val order: Int
) {
    NOUN(1), ADJECTIVE(2), VERB(3)
}

enum class CefrLevel(
    val level: Int
) {
    A1(0), A2(1), B1(2), B2(3), C1(4)
}
