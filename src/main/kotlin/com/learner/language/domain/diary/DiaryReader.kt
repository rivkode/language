package com.learner.language.domain.diary

import com.learner.language.common.pagination.CursorCodec

interface DiaryReader {
    fun getById(diaryId: Long): Diary
    fun findById(diaryId: Long): Diary?
    fun findPublicFeed(cursor: CursorCodec.Cursor?, size: Int, tag: String?): List<Diary>
    fun findPublicFeedTrending(cursor: CursorCodec.Cursor?, size: Int): List<Diary>
    fun findUserFeed(userId: Long, cursor: CursorCodec.Cursor?, size: Int): List<Diary>
    fun findTagsByDiaryId(diaryId: Long): List<String>
    fun findTagsByDiaryIds(diaryIds: Collection<Long>): Map<Long, List<String>>
}
