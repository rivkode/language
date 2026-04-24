package com.learner.language.infrastructure.diary

import com.learner.language.domain.diary.Diary
import com.learner.language.domain.diary.DiaryTag
import com.learner.language.domain.diary.DiaryWriter
import org.springframework.stereotype.Component

@Component
class DiaryWriterImpl(
    private val diaryRepository: DiaryRepository,
    private val diaryTagRepository: DiaryTagRepository,
) : DiaryWriter {

    override fun save(diary: Diary): Diary = diaryRepository.save(diary)

    override fun delete(diary: Diary) {
        diaryRepository.delete(diary)
    }

    override fun saveTags(diaryId: Long, tags: List<String>): List<DiaryTag> {
        if (tags.isEmpty()) return emptyList()
        val entities = tags.map { DiaryTag(diaryId = diaryId, tag = it) }
        return diaryTagRepository.saveAll(entities).toList()
    }

    override fun deleteTagsByDiaryId(diaryId: Long) {
        diaryTagRepository.deleteByDiaryId(diaryId)
    }

    override fun incrementLikeCount(diaryId: Long) {
        diaryRepository.incrementLikeCount(diaryId)
    }

    override fun decrementLikeCount(diaryId: Long) {
        diaryRepository.decrementLikeCount(diaryId)
    }

    override fun incrementCommentCount(diaryId: Long) {
        diaryRepository.incrementCommentCount(diaryId)
    }

    override fun decrementCommentCount(diaryId: Long) {
        diaryRepository.decrementCommentCount(diaryId)
    }
}
