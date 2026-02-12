package com.learner.language.domain.audio

class AudioTranscribeInfo(
    val userId: Long,
    val text: String,
) {
    constructor(audioTranscribe: AudioTranscribe): this(
        userId = audioTranscribe.user.id,
        text = audioTranscribe.text
    )

}