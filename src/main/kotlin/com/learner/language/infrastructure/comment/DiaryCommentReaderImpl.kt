package com.learner.language.infrastructure.comment

import com.learner.language.common.pagination.CursorCodec
import com.learner.language.domain.comment.DiaryComment
import com.learner.language.domain.comment.DiaryCommentReader
import com.learner.language.domain.comment.exception.CommentNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class DiaryCommentReaderImpl(
    private val diaryCommentRepository: DiaryCommentRepository,
    private val commentLikeRepository: CommentLikeRepository,
) : DiaryCommentReader {

    override fun getById(commentId: Long): DiaryComment {
        return diaryCommentRepository.findById(commentId)
            .orElseThrow { CommentNotFoundException("Comment not found: $commentId") }
    }

    override fun findByDiary(diaryId: Long, cursor: CursorCodec.Cursor?, size: Int): List<DiaryComment> {
        val cursorCreatedAt = cursor?.let { LocalDateTime.ofInstant(it.createdAt, ZoneOffset.UTC) }
        val cursorId = cursor?.id
        val pageable = PageRequest.of(0, size)
        return diaryCommentRepository.findByDiaryIdFromCursor(diaryId, cursorCreatedAt, cursorId, pageable)
    }

    override fun existsInDiary(commentId: Long, diaryId: Long): Boolean =
        diaryCommentRepository.existsByIdAndDiaryId(commentId, diaryId)

    override fun findLikedCommentIds(commentIds: Collection<Long>, userId: Long): Set<Long> {
        if (commentIds.isEmpty()) return emptySet()
        return commentLikeRepository.findLikedCommentIds(commentIds, userId).toSet()
    }

    override fun isLikedBy(commentId: Long, userId: Long): Boolean =
        commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)
}
