package com.learner.language.application.shorts

import com.learner.language.interfaces.shorts.ShortsFeedDto

interface ShortsService {
    fun retrieveFeed(userId: Long, request: ShortsFeedDto.FeedRequest): ShortsFeedDto.FeedResponse
}
