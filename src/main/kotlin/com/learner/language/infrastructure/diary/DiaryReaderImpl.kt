package com.learner.language.infrastructure.diary

import com.learner.language.common.pagination.CursorCodec
import com.learner.language.domain.diary.Diary
import com.learner.language.domain.diary.DiaryReader
import com.learner.language.domain.diary.exception.DiaryNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class DiaryReaderImpl(
    private val diaryRepository: DiaryRepository,
    private val diaryTagRepository: DiaryTagRepository,
) : DiaryReader {

    override fun getById(diaryId: Long): Diary {
        return diaryRepository.findById(diaryId)
            .orElseThrow { DiaryNotFoundException("Diary not found: $diaryId") }
    }

    override fun findById(diaryId: Long): Diary? {
        return diaryRepository.findById(diaryId).orElse(null)
    }

    override fun findPublicFeed(cursor: CursorCodec.Cursor?, size: Int, tag: String?): List<Diary> {
        val cursorCreatedAt = cursor?.let { LocalDateTime.ofInstant(it.createdAt, ZoneOffset.UTC) }
        val cursorId = cursor?.id
        val pageable = PageRequest.of(0, size)
        return if (tag.isNullOrBlank()) {
            diaryRepository.findPublicFeedRecent(cursorCreatedAt, cursorId, pageable)
        } else {
            diaryRepository.findPublicFeedByTagRecent(tag, cursorCreatedAt, cursorId, pageable)
        }
    }

    override fun findPublicFeedTrending(cursor: CursorCodec.Cursor?, size: Int): List<Diary> {
        val pageable = PageRequest.of(0, size)
        val cursorLikeCount = cursor?.let {
            // trending cursor: id(ts) 자리를 likeCount로 재사용 — cursor.createdAt.epochMilli를 likeCount로 간주
            it.createdAt.toEpochMilli().toInt()
        }
        val cursorId = cursor?.id
        return diaryRepository.findPublicFeedTrending(cursorLikeCount, cursorId, pageable)
    }

    override fun findUserFeed(userId: Long, cursor: CursorCodec.Cursor?, size: Int): List<Diary> {
        val cursorCreatedAt = cursor?.let { LocalDateTime.ofInstant(it.createdAt, ZoneOffset.UTC) }
        val cursorId = cursor?.id
        val pageable = PageRequest.of(0, size)
        return diaryRepository.findUserFeedRecent(userId, cursorCreatedAt, cursorId, pageable)
    }

    override fun findTagsByDiaryId(diaryId: Long): List<String> {
        return diaryTagRepository.findByDiaryId(diaryId).map { it.tag }
    }

    override fun findTagsByDiaryIds(diaryIds: Collection<Long>): Map<Long, List<String>> {
        if (diaryIds.isEmpty()) return emptyMap()
        return diaryTagRepository.findByDiaryIdIn(diaryIds)
            .groupBy { it.diaryId }
            .mapValues { (_, tags) -> tags.map { it.tag } }
    }
}
