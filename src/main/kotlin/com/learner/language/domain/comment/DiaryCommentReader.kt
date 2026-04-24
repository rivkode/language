package com.learner.language.domain.comment

import com.learner.language.common.pagination.CursorCodec

interface DiaryCommentReader {
    fun getById(commentId: Long): DiaryComment
    fun findByDiary(diaryId: Long, cursor: CursorCodec.Cursor?, size: Int): List<DiaryComment>
    fun existsInDiary(commentId: Long, diaryId: Long): Boolean
    fun findLikedCommentIds(commentIds: Collection<Long>, userId: Long): Set<Long>
    fun isLikedBy(commentId: Long, userId: Long): Boolean
}
