package com.learner.language.application.diary

import com.learner.language.common.pagination.CursorCodec
import com.learner.language.common.pagination.CursorPage
import com.learner.language.common.pagination.PageInfo
import com.learner.language.domain.diary.AccentToneResolver
import com.learner.language.domain.diary.Diary
import com.learner.language.domain.diary.DiaryLikeReader
import com.learner.language.domain.diary.DiaryReader
import com.learner.language.domain.diary.DiaryWriter
import com.learner.language.domain.diary.exception.DiaryForbiddenException
import com.learner.language.domain.diary.exception.InvalidLineCountException
import com.learner.language.domain.diary.exception.InvalidLineLengthException
import com.learner.language.domain.user.User
import com.learner.language.domain.user.UserReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset

@Service
class DiaryFacade(
    private val diaryReader: DiaryReader,
    private val diaryWriter: DiaryWriter,
    private val diaryLikeReader: DiaryLikeReader,
    private val userReader: UserReader,
) {

    @Transactional
    fun create(command: DiaryCreateCommand): DiaryView {
        validateLines(command.lines)
        validateTags(command.tags)

        val trimmed = command.lines.map { it.trim() }
        val diary = Diary(
            userId = command.authorUserId,
            line1 = trimmed[0],
            line2 = trimmed[1],
            line3 = trimmed[2],
            isPublic = command.isPublic,
        )
        val saved = diaryWriter.save(diary)

        val normalizedTags = command.tags.map { it.trim() }.filter { it.isNotBlank() }.distinct()
        diaryWriter.saveTags(saved.id, normalizedTags)

        val author = userReader.getUserById(command.authorUserId)
        return toView(saved, normalizedTags, author, userLiked = false)
    }

    @Transactional(readOnly = true)
    fun getDetail(diaryId: Long, viewerUserId: Long): DiaryView {
        val diary = diaryReader.getById(diaryId)
        val tags = diaryReader.findTagsByDiaryId(diary.id)
        val author = userReader.getUserById(diary.userId)
        val userLiked = diaryLikeReader.isLikedBy(diary.id, viewerUserId)
        return toView(diary, tags, author, userLiked = userLiked)
    }

    @Transactional(readOnly = true)
    fun getFeed(query: DiaryFeedQuery, viewerUserId: Long): CursorPage<DiaryView> {
        val size = query.size.coerceIn(1, MAX_PAGE_SIZE)
        val cursor = CursorCodec.decode(query.cursor)
        val diaries = when (query.sort) {
            DiaryFeedSort.RECENT -> diaryReader.findPublicFeed(cursor, size + 1, query.tag)
            DiaryFeedSort.TRENDING -> diaryReader.findPublicFeedTrending(cursor, size + 1)
        }
        return buildPage(diaries, size, query.sort, viewerUserId)
    }

    @Transactional(readOnly = true)
    fun getMyFeed(userId: Long, cursorStr: String?, size: Int): CursorPage<DiaryView> {
        val capped = size.coerceIn(1, MAX_PAGE_SIZE)
        val cursor = CursorCodec.decode(cursorStr)
        val diaries = diaryReader.findUserFeed(userId, cursor, capped + 1)
        return buildPage(diaries, capped, DiaryFeedSort.RECENT, userId)
    }

    @Transactional
    fun delete(diaryId: Long, authorUserId: Long) {
        val diary = diaryReader.getById(diaryId)
        if (!diary.isOwnedBy(authorUserId)) {
            throw DiaryForbiddenException("본인 일기만 삭제 가능합니다.")
        }
        diaryWriter.deleteTagsByDiaryId(diary.id)
        diaryWriter.delete(diary)
    }

    private fun buildPage(
        diaries: List<Diary>,
        size: Int,
        sort: DiaryFeedSort,
        viewerUserId: Long,
    ): CursorPage<DiaryView> {
        val hasNext = diaries.size > size
        val page = if (hasNext) diaries.take(size) else diaries
        val pageIds = page.map { it.id }
        val tagsByDiary = diaryReader.findTagsByDiaryIds(pageIds)
        val likedDiaryIds = diaryLikeReader.findLikedDiaryIds(pageIds, viewerUserId)
        val authors = userReader.let { reader ->
            page.map { it.userId }.distinct().associateWith { reader.getUserById(it) }
        }
        val items = page.map { diary ->
            toView(
                diary = diary,
                tags = tagsByDiary[diary.id].orEmpty(),
                author = authors.getValue(diary.userId),
                userLiked = diary.id in likedDiaryIds,
            )
        }
        val nextCursor = if (hasNext) encodeCursor(page.last(), sort) else null
        return CursorPage(items, PageInfo(nextCursor, hasNext))
    }

    private fun encodeCursor(last: Diary, sort: DiaryFeedSort): String {
        return when (sort) {
            DiaryFeedSort.RECENT ->
                CursorCodec.encode(last.createdAt.toInstant(ZoneOffset.UTC), last.id)
            DiaryFeedSort.TRENDING ->
                // trending: likeCount를 cursor의 createdAt 자리에 담아 유지 (millis로 치환)
                CursorCodec.encode(
                    java.time.Instant.ofEpochMilli(last.likeCount.toLong()),
                    last.id,
                )
        }
    }

    private fun toView(diary: Diary, tags: List<String>, author: User, userLiked: Boolean): DiaryView {
        return DiaryView(
            diaryId = diary.id,
            author = DiaryAuthorView(
                userId = author.id,
                username = author.username,
                avatarUrl = null,
            ),
            lines = diary.lines(),
            createdAt = diary.createdAt.toInstant(ZoneOffset.UTC),
            tags = tags,
            likeCount = diary.likeCount,
            commentCount = diary.commentCount,
            voiceParticipantCount = 0,
            userLiked = userLiked,
            isPublic = diary.isPublic,
            accentTone = AccentToneResolver.of(diary.id),
        )
    }

    private fun validateLines(lines: List<String>) {
        if (lines.size != Diary.LINE_COUNT) {
            throw InvalidLineCountException("3줄 일기는 반드시 ${Diary.LINE_COUNT}개 라인이어야 합니다.")
        }
        lines.forEachIndexed { idx, line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                throw InvalidLineLengthException("${idx + 1}번째 라인이 비어있습니다.")
            }
            if (trimmed.length > Diary.LINE_MAX_LENGTH) {
                throw InvalidLineLengthException(
                    "${idx + 1}번째 라인이 최대 길이(${Diary.LINE_MAX_LENGTH})를 초과했습니다.",
                )
            }
        }
    }

    private fun validateTags(tags: List<String>) {
        if (tags.size > Diary.TAG_MAX_COUNT) {
            throw InvalidLineLengthException("태그는 최대 ${Diary.TAG_MAX_COUNT}개까지 허용됩니다.")
        }
        tags.forEach { tag ->
            val trimmed = tag.trim()
            if (trimmed.isEmpty() || trimmed.length > Diary.TAG_MAX_LENGTH) {
                throw InvalidLineLengthException(
                    "태그는 1~${Diary.TAG_MAX_LENGTH}자여야 합니다.",
                )
            }
        }
    }

    companion object {
        private const val MAX_PAGE_SIZE = 50
    }
}
