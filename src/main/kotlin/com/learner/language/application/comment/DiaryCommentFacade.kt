package com.learner.language.application.comment

import com.learner.language.application.diary.DiaryAuthorView
import com.learner.language.common.pagination.CursorCodec
import com.learner.language.common.pagination.CursorPage
import com.learner.language.common.pagination.PageInfo
import com.learner.language.domain.comment.DiaryComment
import com.learner.language.domain.comment.DiaryCommentReader
import com.learner.language.domain.comment.DiaryCommentWriter
import com.learner.language.domain.comment.exception.CommentForbiddenException
import com.learner.language.domain.comment.exception.CommentNotFoundException
import com.learner.language.domain.comment.exception.CommentTooLongException
import com.learner.language.domain.diary.DiaryReader
import com.learner.language.domain.diary.DiaryWriter
import com.learner.language.domain.user.UserReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset

@Service
class DiaryCommentFacade(
    private val diaryReader: DiaryReader,
    private val diaryWriter: DiaryWriter,
    private val commentReader: DiaryCommentReader,
    private val commentWriter: DiaryCommentWriter,
    private val userReader: UserReader,
) {

    @Transactional
    fun create(command: CreateCommentCommand): DiaryCommentView {
        val diary = diaryReader.getById(command.diaryId)
        validateText(command.text)

        if (command.parentCommentId != null) {
            val exists = commentReader.existsInDiary(command.parentCommentId, diary.id)
            if (!exists) {
                throw CommentNotFoundException("Parent comment not found in diary: ${command.parentCommentId}")
            }
        }

        val entity = DiaryComment(
            diaryId = diary.id,
            authorUserId = command.authorUserId,
            text = command.text.trim(),
            parentCommentId = command.parentCommentId,
        )
        val saved = commentWriter.save(entity)
        diaryWriter.incrementCommentCount(diary.id)

        val author = userReader.getUserById(command.authorUserId)
        return DiaryCommentView(
            commentId = saved.id,
            diaryId = saved.diaryId,
            author = DiaryAuthorView(author.id, author.username, null),
            text = saved.text,
            createdAt = saved.createdAt.toInstant(ZoneOffset.UTC),
            parentCommentId = saved.parentCommentId,
            likeCount = saved.likeCount,
            userLiked = false,
        )
    }

    @Transactional(readOnly = true)
    fun list(diaryId: Long, cursorStr: String?, size: Int, viewerUserId: Long): CursorPage<DiaryCommentView> {
        diaryReader.getById(diaryId)
        val capped = size.coerceIn(1, MAX_PAGE_SIZE)
        val cursor = CursorCodec.decode(cursorStr)
        val comments = commentReader.findByDiary(diaryId, cursor, capped + 1)
        val hasNext = comments.size > capped
        val page = if (hasNext) comments.take(capped) else comments
        val likedIds = commentReader.findLikedCommentIds(page.map { it.id }, viewerUserId)
        val authors = page.map { it.authorUserId }.distinct()
            .associateWith { userReader.getUserById(it) }

        val items = page.map { comment ->
            val author = authors.getValue(comment.authorUserId)
            DiaryCommentView(
                commentId = comment.id,
                diaryId = comment.diaryId,
                author = DiaryAuthorView(author.id, author.username, null),
                text = comment.text,
                createdAt = comment.createdAt.toInstant(ZoneOffset.UTC),
                parentCommentId = comment.parentCommentId,
                likeCount = comment.likeCount,
                userLiked = comment.id in likedIds,
            )
        }
        val nextCursor = if (hasNext) {
            CursorCodec.encode(page.last().createdAt.toInstant(ZoneOffset.UTC), page.last().id)
        } else null
        return CursorPage(items, PageInfo(nextCursor, hasNext))
    }

    @Transactional
    fun delete(commentId: Long, userId: Long) {
        val comment = commentReader.getById(commentId)
        if (!comment.isAuthoredBy(userId)) {
            throw CommentForbiddenException("본인 댓글만 삭제 가능합니다.")
        }
        val diaryId = comment.diaryId
        commentWriter.delete(comment)
        diaryWriter.decrementCommentCount(diaryId)
    }

    private fun validateText(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            throw CommentTooLongException("댓글 내용이 비어있습니다.")
        }
        if (trimmed.length > DiaryComment.MAX_TEXT_LENGTH) {
            throw CommentTooLongException("댓글은 최대 ${DiaryComment.MAX_TEXT_LENGTH}자입니다.")
        }
    }

    companion object {
        private const val MAX_PAGE_SIZE = 100
    }
}
