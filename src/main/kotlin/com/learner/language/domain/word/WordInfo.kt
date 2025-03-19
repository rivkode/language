package com.learner.language.domain.word

data class WordInfo(
    val id: Long,
    val label: String,
    val part: Part,
    val description: String,
    val level: CefrLevel
) {
    constructor(word: Word) : this(
        id = word.id,
        label = word.label,
        part = word.part,
        description = word.description,
        level = word.level
    )

    companion object {
        fun from(words: List<Word>) : List<WordInfo> {
            return words.map { WordInfo(it) }
        }
    }
}
