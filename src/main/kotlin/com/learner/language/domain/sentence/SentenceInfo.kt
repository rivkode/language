package com.learner.language.domain.sentence

data class SentenceInfo(
    val userId: Long,
    val userText: String
) {
    constructor(sentence: Sentence) : this(
        userId = sentence.user.id,
        userText = sentence.userText
    )

}
