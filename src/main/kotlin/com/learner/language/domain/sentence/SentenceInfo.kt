package com.learner.language.domain.sentence

import com.learner.language.domain.word.WordInfo

data class SentenceInfo(
    val sentenceId: Long,
    val userId: Long,
    val userText: String,
    val wordList: List<WordInfo> = listOf()
) {
    constructor(sentence: Sentence) : this(
        sentenceId = sentence.id,
        userId = sentence.user.id,
        userText = sentence.userSentence
    )

    companion object {
        fun from(sentences: List<Sentence>) : List<SentenceInfo> {
            return sentences.map { SentenceInfo(it) }
        }

        fun from(sentences: List<Sentence>, wordList: List<WordInfo>) : List<SentenceInfo> {
            return sentences.map { SentenceInfo(it.id, it.user.id, it.userSentence, wordList) }
        }
    }

}
