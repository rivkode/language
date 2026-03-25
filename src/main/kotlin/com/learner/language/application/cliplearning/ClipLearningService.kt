package com.learner.language.application.cliplearning

import com.learner.language.interfaces.cliplearning.ClipLearningClipDto
import com.learner.language.interfaces.cliplearning.ClipLearningFeedDto
import com.learner.language.interfaces.cliplearning.ClipLearningProgressDto
import com.learner.language.interfaces.cliplearning.ClipLearningSaveDto

interface ClipLearningService {
    fun retrieveFeed(userId: Long, request: ClipLearningFeedDto.FeedRequest): ClipLearningFeedDto.FeedResponse
    fun retrieveClip(userId: Long, clipId: Long): ClipLearningClipDto.ClipDetailResponse
    fun saveClip(userId: Long, clipId: Long, request: ClipLearningSaveDto.SaveRequest): ClipLearningSaveDto.SaveResponse
    fun updateProgress(userId: Long, request: ClipLearningProgressDto.ProgressRequest): ClipLearningProgressDto.ProgressResponse
}
