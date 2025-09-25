package com.learner.language.domain.chat

class AudioRecordInfo(
    val userId: Long,
    val speechText: String,
    val filePath: String
) {
    constructor(audioRecord: AudioRecord): this (
        userId = audioRecord.user.id,
        speechText = audioRecord.speechText,
        filePath = audioRecord.filePath
    )

}