package com.learner.language.domain.word

data class WordInfo(
    val id: Long,
    val label: String,
    val part: Part,
    val enMeaning: String,
    val krExample: String?,
    val enExample: String?,
    val ipa: String?,
    val level: CefrLevel
) {
    constructor(word: Word) : this(
        id = word.id,
        label = word.label,
        part = word.part,
        enMeaning = word.enMeaning,
        krExample = word.krExample,
        enExample = word.enExample,
        ipa = word.ipa,
        level = word.level
    )

    companion object {
        fun from(words: List<Word>) : List<WordInfo> {
            return words.map { WordInfo(it) }
        }
    }
}
