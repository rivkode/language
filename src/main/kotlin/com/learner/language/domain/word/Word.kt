package com.learner.language.domain.word

import com.learner.language.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "word")
class Word(

    @Column(name = "label", nullable = false)
    var label: String,

    @Column(name = "en_meaning")
    var enMeaning: String,

    @Column(name = "kr_example")
    var krExample: String?,

    @Column(name = "en_example")
    var enExample: String?,

    @Column(name = "ipa")
    var ipa: String?,

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
    NOUN(1), VERB(2), ADJECTIVE(3)
}

enum class CefrLevel(
    val level: Int
) {
    A1(0), A2(1), B1(2), B2(3), C1(4)
}
