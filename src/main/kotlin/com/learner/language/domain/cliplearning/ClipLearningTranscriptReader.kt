package com.learner.language.domain.cliplearning

interface ClipLearningTranscriptReader {
    fun retrieveTranscript(videoId: String): ClipLearningTranscriptInfo
}
