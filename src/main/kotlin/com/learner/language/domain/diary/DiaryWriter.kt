package com.learner.language.domain.diary

interface DiaryWriter {
    fun save(diary: Diary): Diary
    fun delete(diary: Diary)
    fun saveTags(diaryId: Long, tags: List<String>): List<DiaryTag>
    fun deleteTagsByDiaryId(diaryId: Long)
    fun incrementLikeCount(diaryId: Long)
    fun decrementLikeCount(diaryId: Long)
    fun incrementCommentCount(diaryId: Long)
    fun decrementCommentCount(diaryId: Long)
}
