package com.learner.language.domain.sentence

import com.learner.language.domain.audio.AudioSpeech
import com.learner.language.domain.audio.AudioSpeechInfo
import com.learner.language.domain.word.WordInfo

data class SentenceInfo(
    val sentenceId: Long,
    val userId: Long,
    val text: String,
    val wordList: List<WordInfo>? = null,
    val audioSpeechInfo: AudioSpeechInfo? = null
) {
    constructor(sentence: Sentence) : this(
        sentenceId = sentence.id,
        userId = sentence.user.id,
        text = sentence.userSentence,
        wordList = null,
        audioSpeechInfo = null
    )

    companion object {
        fun from(sentences: List<Sentence>) : List<SentenceInfo> {
            return sentences.map { SentenceInfo(it) }
        }

        fun from(sentences: List<Sentence>, wordList: List<WordInfo>) : List<SentenceInfo> {
            return sentences.map { SentenceInfo(it.id, it.user.id, it.userSentence, wordList, null) }
        }

        fun from(sentence: Sentence, audioSpeech: AudioSpeech): SentenceInfo {
            return SentenceInfo(
                sentenceId = sentence.id,
                userId = sentence.user.id,
                text = sentence.userSentence,
                audioSpeechInfo = AudioSpeechInfo(audioSpeech)
            )
        }
    }

}
