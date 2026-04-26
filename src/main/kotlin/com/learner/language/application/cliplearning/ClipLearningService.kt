package com.learner.language.application.cliplearning

import com.learner.language.interfaces.cliplearning.ClipLearningClipPageItem
import com.learner.language.interfaces.cliplearning.ClipLearningFeedDto
import com.learner.language.interfaces.cliplearning.ClipLearningProgressDto
import com.learner.language.interfaces.cliplearning.ClipLearningSaveDto
import com.learner.language.interfaces.cliplearning.ClipLearningTranscriptDto

interface ClipLearningService {
    fun retrieveFeed(userId: Long, request: ClipLearningFeedDto.FeedRequest): ClipLearningFeedDto.FeedResponse
    fun retrieveClip(userId: Long, clipId: Long): ClipLearningClipPageItem
    fun retrieveTranscript(request: ClipLearningTranscriptDto.TranscriptRequest): ClipLearningTranscriptDto.TranscriptResponse
    fun saveClip(userId: Long, clipId: Long, request: ClipLearningSaveDto.SaveRequest): ClipLearningSaveDto.SaveResponse
    fun updateProgress(userId: Long, request: ClipLearningProgressDto.ProgressRequest): ClipLearningProgressDto.ProgressResponse
}
