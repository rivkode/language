package com.learner.language.domain.word

data class WordInfo(
    val id: Long,
    val label: String
) {
    constructor(word: Word) : this(
        id = word.id,
        label = word.label
    )
}
