package com.learner.language.application.shorts

import com.learner.language.infrastructure.shorts.ShortsFeedQueryRepository
import com.learner.language.infrastructure.shorts.ShortsFeedRow
import com.learner.language.infrastructure.shorts.UserSavedShortRepository
import com.learner.language.interfaces.shorts.ShortsFeedDto
import com.learner.language.interfaces.shorts.ShortsFeedItem
import com.learner.language.interfaces.shorts.ShortsPagingPayload
import com.learner.language.interfaces.shorts.ShortsUserStatePayload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ShortsServiceImpl(
    private val shortsFeedQueryRepository: ShortsFeedQueryRepository,
    private val userSavedShortRepository: UserSavedShortRepository,
) : ShortsService {

    override fun retrieveFeed(userId: Long, request: ShortsFeedDto.FeedRequest): ShortsFeedDto.FeedResponse {
        val rows = shortsFeedQueryRepository.findFeedRows(
            cursor = request.cursor,
            size = request.size + 1,
        )
        val hasNext = rows.size > request.size
        val pageRows = if (hasNext) rows.take(request.size) else rows
        val shortIds = pageRows.map { it.shortId }

        val savedShortIds = findSavedShortIds(userId, shortIds)

        return ShortsFeedDto.FeedResponse(
            items = pageRows.map { row ->
                row.toFeedItem(saved = savedShortIds.contains(row.shortId))
            },
            paging = ShortsPagingPayload(
                nextCursor = if (hasNext && pageRows.isNotEmpty()) pageRows.last().shortId.toString() else null,
                hasNext = hasNext,
            )
        )
    }

    private fun findSavedShortIds(userId: Long, shortIds: List<Long>): Set<Long> {
        if (shortIds.isEmpty()) {
            return emptySet()
        }
        return userSavedShortRepository.findAllByUserIdAndShortIdIn(userId, shortIds)
            .map { it.short.id }
            .toSet()
    }

    private fun ShortsFeedRow.toFeedItem(saved: Boolean): ShortsFeedItem {
        return ShortsFeedItem(
            shortId = shortId,
            youtubeVideoId = youtubeVideoId,
            sourceUrl = sourceUrl,
            userState = ShortsUserStatePayload(saved = saved),
        )
    }
}
