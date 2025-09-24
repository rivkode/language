package com.learner.language.domain.chat

class AudioTranscribeInfo(
    val userId: Long,
    val text: String,
) {
    constructor(audioTranscribe: AudioTranscribe): this(
        userId = audioTranscribe.user.id,
        text = audioTranscribe.text
    )

}