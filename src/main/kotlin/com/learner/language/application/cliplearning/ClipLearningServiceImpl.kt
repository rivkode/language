package com.learner.language.application.cliplearning

import com.learner.language.domain.cliplearning.ClipLearningClip
import com.learner.language.domain.cliplearning.ClipLearningTranscriptReader
import com.learner.language.domain.cliplearning.UserClipLearningProgress
import com.learner.language.domain.cliplearning.UserSavedClip
import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.cliplearning.ClipLearningClipRepository
import com.learner.language.infrastructure.cliplearning.ClipLearningFeedQueryRepository
import com.learner.language.infrastructure.cliplearning.ClipLearningFeedRow
import com.learner.language.infrastructure.cliplearning.UserClipLearningProgressRepository
import com.learner.language.infrastructure.cliplearning.UserSavedClipRepository
import com.learner.language.interfaces.cliplearning.ClipLearningClipPageItem
import com.learner.language.interfaces.cliplearning.ClipLearningFeedDto
import com.learner.language.interfaces.cliplearning.ClipLearningPagingPayload
import com.learner.language.interfaces.cliplearning.ClipLearningProgressDto
import com.learner.language.interfaces.cliplearning.ClipLearningSaveDto
import com.learner.language.interfaces.cliplearning.ClipLearningTranscriptDto
import com.learner.language.interfaces.cliplearning.ClipLearningUserStatePayload
import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class ClipLearningServiceImpl(
    private val clipLearningFeedQueryRepository: ClipLearningFeedQueryRepository,
    private val clipLearningClipRepository: ClipLearningClipRepository,
    private val userSavedClipRepository: UserSavedClipRepository,
    private val userClipLearningProgressRepository: UserClipLearningProgressRepository,
    private val userReader: UserReader,
    private val clipLearningTranscriptReader: ClipLearningTranscriptReader,
) : ClipLearningService {

    override fun retrieveFeed(userId: Long, request: ClipLearningFeedDto.FeedRequest): ClipLearningFeedDto.FeedResponse {
        val rows = clipLearningFeedQueryRepository.findFeedRows(
            category = request.category,
            cursor = request.cursor,
            size = request.size + 1
        )
        val hasNext = rows.size > request.size
        val pageRows = if (hasNext) rows.take(request.size) else rows
        val clipIds = pageRows.map { it.clipId }

        val savedClipIds = findSavedClipIds(userId, clipIds)

        return ClipLearningFeedDto.FeedResponse(
            items = pageRows.map { row ->
                row.toClipPageItem(saved = savedClipIds.contains(row.clipId))
            },
            paging = ClipLearningPagingPayload(
                nextCursor = if (hasNext && pageRows.isNotEmpty()) pageRows.last().clipId.toString() else null,
                hasNext = hasNext
            )
        )
    }

    override fun retrieveClip(userId: Long, clipId: Long): ClipLearningClipPageItem {
        val row = clipLearningFeedQueryRepository.findClipRowByClipId(clipId)
            ?: throw NotFoundException(ErrorCode.NOT_FOUND, "clipId=$clipId clip not found")
        val saved = userSavedClipRepository.findByUserIdAndClipId(userId, clipId).isPresent

        return row.toClipPageItem(saved = saved)
    }

    override fun retrieveTranscript(
        request: ClipLearningTranscriptDto.TranscriptRequest
    ): ClipLearningTranscriptDto.TranscriptResponse {
        val transcript = clipLearningTranscriptReader.retrieveTranscript(request.youtubeVideoId)
        return ClipLearningTranscriptDto.TranscriptResponse(transcript)
    }

    @Transactional
    override fun saveClip(
        userId: Long,
        clipId: Long,
        request: ClipLearningSaveDto.SaveRequest
    ): ClipLearningSaveDto.SaveResponse {
        val clip = getClip(clipId)
        val saved = request.saved ?: false

        if (!saved) {
            userSavedClipRepository.deleteByUserIdAndClipId(userId, clipId)
            return ClipLearningSaveDto.SaveResponse(
                clipId = clipId,
                saved = false,
                savedAt = null
            )
        }

        val existing = userSavedClipRepository.findByUserIdAndClipId(userId, clipId).orElse(null)
        val savedClip = existing ?: userSavedClipRepository.save(
            UserSavedClip(
                user = userReader.getUserById(userId),
                clip = clip
            )
        )

        return ClipLearningSaveDto.SaveResponse(
            clipId = clipId,
            saved = true,
            savedAt = savedClip.createdAt.toUtcString()
        )
    }

    @Transactional
    override fun updateProgress(
        userId: Long,
        request: ClipLearningProgressDto.ProgressRequest
    ): ClipLearningProgressDto.ProgressResponse {
        val clipId = request.clipId ?: 0L
        val clip = getClip(clipId)
        val progress = userClipLearningProgressRepository.findByUserIdAndClipId(userId, clipId).orElse(null)
            ?: UserClipLearningProgress(
                user = userReader.getUserById(userId),
                clip = clip
            )

        progress.lastViewedPositionMs = request.lastViewedPositionMs ?: 0L
        progress.repeatEnabled = request.repeatEnabled ?: false
        progress.translationVisible = request.translationVisible ?: false
        progress.completed = request.completed ?: false

        val savedProgress = userClipLearningProgressRepository.save(progress)

        return ClipLearningProgressDto.ProgressResponse(
            clipId = clipId,
            updatedAt = savedProgress.updatedAt.toUtcString()
        )
    }

    private fun getClip(clipId: Long): ClipLearningClip {
        return clipLearningClipRepository.findById(clipId)
            .orElseThrow {
                NotFoundException(ErrorCode.NOT_FOUND, "clipId=$clipId clip not found")
            }
    }

    private fun findSavedClipIds(userId: Long, clipIds: List<Long>): Set<Long> {
        if (clipIds.isEmpty()) {
            return emptySet()
        }

        return userSavedClipRepository.findAllByUserIdAndClipIdIn(userId, clipIds)
            .map { it.clip.id }
            .toSet()
    }

    private fun ClipLearningFeedRow.toClipPageItem(saved: Boolean): ClipLearningClipPageItem {
        return ClipLearningClipPageItem(
            clipId = clipId,
            youtubeVideoId = youtubeVideoId,
            clipStartMs = clipStartMs,
            clipEndMs = clipEndMs,
            title = title,
            userState = ClipLearningUserStatePayload(saved = saved),
        )
    }

    private fun java.time.LocalDateTime.toUtcString(): String {
        return this.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}
