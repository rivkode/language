package com.learner.language.application.cliplearning

import com.learner.language.domain.cliplearning.ClipLearningClip
import com.learner.language.domain.cliplearning.UserClipLearningProgress
import com.learner.language.domain.cliplearning.UserSavedClip
import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.cliplearning.ClipLearningClipRepository
import com.learner.language.infrastructure.cliplearning.ClipLearningFeedQueryRepository
import com.learner.language.infrastructure.cliplearning.ClipLearningFeedRow
import com.learner.language.infrastructure.cliplearning.ClipLearningVocabularyRepository
import com.learner.language.infrastructure.cliplearning.UserClipLearningProgressRepository
import com.learner.language.infrastructure.cliplearning.UserSavedClipRepository
import com.learner.language.interfaces.cliplearning.ClipLearningClipDto
import com.learner.language.interfaces.cliplearning.ClipLearningClipPageItem
import com.learner.language.interfaces.cliplearning.ClipLearningExplanationPayload
import com.learner.language.interfaces.cliplearning.ClipLearningFeedDto
import com.learner.language.interfaces.cliplearning.ClipLearningPagingPayload
import com.learner.language.interfaces.cliplearning.ClipLearningProgressDto
import com.learner.language.interfaces.cliplearning.ClipLearningSaveDto
import com.learner.language.interfaces.cliplearning.ClipLearningUserStatePayload
import com.learner.language.interfaces.cliplearning.ClipLearningVocabularyItemPayload
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
    private val clipLearningVocabularyRepository: ClipLearningVocabularyRepository,
    private val userSavedClipRepository: UserSavedClipRepository,
    private val userClipLearningProgressRepository: UserClipLearningProgressRepository,
    private val userReader: UserReader
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

        val vocabularyByClipId = findVocabularyByClipIds(clipIds)
        val savedClipIds = findSavedClipIds(userId, clipIds)
        val progressByClipId = findProgressByClipId(userId, clipIds)

        return ClipLearningFeedDto.FeedResponse(
            items = pageRows.map { row ->
                row.toClipPageItem(
                    saved = savedClipIds.contains(row.clipId),
                    progress = progressByClipId[row.clipId],
                    vocabulary = vocabularyByClipId[row.clipId].orEmpty()
                )
            },
            paging = ClipLearningPagingPayload(
                nextCursor = if (hasNext && pageRows.isNotEmpty()) pageRows.last().clipId.toString() else null,
                hasNext = hasNext
            )
        )
    }

    override fun retrieveClip(userId: Long, clipId: Long): ClipLearningClipDto.ClipDetailResponse {
        val row = clipLearningFeedQueryRepository.findClipRowByClipId(clipId)
            ?: throw NotFoundException(ErrorCode.NOT_FOUND, "clipId=$clipId clip not found")
        val vocabulary = clipLearningVocabularyRepository.findAllByClipIdOrderByDisplayOrderAsc(clipId)
            .map { ClipLearningVocabularyItemPayload(word = it.word, meaning = it.meaning) }
        val saved = userSavedClipRepository.findByUserIdAndClipId(userId, clipId).isPresent
        val progress = userClipLearningProgressRepository.findByUserIdAndClipId(userId, clipId).orElse(null)

        return ClipLearningClipDto.ClipDetailResponse(
            row.toClipPageItem(
                saved = saved,
                progress = progress,
                vocabulary = vocabulary
            )
        )
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

    private fun findVocabularyByClipIds(clipIds: List<Long>): Map<Long, List<ClipLearningVocabularyItemPayload>> {
        if (clipIds.isEmpty()) {
            return emptyMap()
        }

        return clipLearningVocabularyRepository.findAllByClipIdInOrderByClipIdAscDisplayOrderAsc(clipIds)
            .groupBy { it.clip.id }
            .mapValues { (_, vocabularies) ->
                vocabularies.map {
                    ClipLearningVocabularyItemPayload(
                        word = it.word,
                        meaning = it.meaning
                    )
                }
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

    private fun findProgressByClipId(userId: Long, clipIds: List<Long>): Map<Long, UserClipLearningProgress> {
        if (clipIds.isEmpty()) {
            return emptyMap()
        }

        return userClipLearningProgressRepository.findAllByUserIdAndClipIdIn(userId, clipIds)
            .associateBy { it.clip.id }
    }

    private fun ClipLearningFeedRow.toClipPageItem(
        saved: Boolean,
        progress: UserClipLearningProgress?,
        vocabulary: List<ClipLearningVocabularyItemPayload>
    ): ClipLearningClipPageItem {
        return ClipLearningClipPageItem(
            clipId = clipId,
            sourceVideoId = sourceVideoId,
            youtubeVideoId = youtubeVideoId,
            sourceUrl = sourceUrl,
            title = title,
            category = category,
            channelName = channelName,
            clipStartMs = clipStartMs,
            clipEndMs = clipEndMs,
            primarySentence = primarySentence,
            userState = progress.toUserState(saved),
            thumbnailUrl = thumbnailUrl,
            translation = translation,
            explanation = ClipLearningExplanationPayload(
                summary = explanationSummary ?: "",
                grammarPoints = emptyList(),
                vocabulary = vocabulary,
                usageTip = usageTip ?: ""
            ),
            clipDurationMs = clipDurationMs
        )
    }

    private fun UserClipLearningProgress?.toUserState(saved: Boolean): ClipLearningUserStatePayload {
        return ClipLearningUserStatePayload(
            saved = saved,
            completed = this?.completed ?: false,
            masteryLevel = when {
                this?.completed == true -> "review"
                saved -> "learning"
                else -> "new"
            },
            lastViewedAt = this?.updatedAt?.toUtcString(),
            repeatCount = 0,
            shadowingCount = 0
        )
    }

    private fun java.time.LocalDateTime.toUtcString(): String {
        return this.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}
