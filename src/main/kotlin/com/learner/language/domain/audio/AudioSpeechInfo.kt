package com.learner.language.domain.audio

class AudioSpeechInfo(
    val userId: Long,
    val speechText: String,
    val filePath: String
) {
    constructor(audioSpeech: AudioSpeech): this (
        userId = audioSpeech.user.id,
        speechText = audioSpeech.text,
        filePath = audioSpeech.filePath
    )

}