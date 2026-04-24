package com.learner.language.interfaces.diary

import com.learner.language.application.diary.DiaryFacade
import com.learner.language.application.diary.DiaryFeedQuery
import com.learner.language.application.diary.DiaryFeedSort
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/diaries")
class DiaryController(
    private val diaryFacade: DiaryFacade,
) {

    @GetMapping("/feed")
    fun getFeed(
        @LoginUser userId: Long,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "recent") sort: String,
        @RequestParam(required = false) category: String?,
    ): ResponseEntity<DiaryDto.FeedResponse> {
        val query = DiaryFeedQuery(
            cursor = cursor,
            size = size,
            sort = DiaryFeedSort.from(sort),
            tag = category,
        )
        val page = diaryFacade.getFeed(query, userId)
        return ResponseEntity.ok(DiaryDto.FeedResponse.from(page))
    }

    @GetMapping("/me")
    fun getMyFeed(
        @LoginUser userId: Long,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(required = false, defaultValue = "10") size: Int,
    ): ResponseEntity<DiaryDto.FeedResponse> {
        val page = diaryFacade.getMyFeed(userId, cursor, size)
        return ResponseEntity.ok(DiaryDto.FeedResponse.from(page))
    }

    @GetMapping("/{diaryId}")
    fun getDetail(
        @LoginUser userId: Long,
        @PathVariable diaryId: Long,
    ): ResponseEntity<DiaryDto.DiaryResponse> {
        val view = diaryFacade.getDetail(diaryId, userId)
        return ResponseEntity.ok(DiaryDto.DiaryResponse.from(view))
    }

    @PostMapping
    fun create(
        @LoginUser userId: Long,
        @Valid @RequestBody request: DiaryDto.CreateRequest,
    ): ResponseEntity<DiaryDto.DiaryResponse> {
        val view = diaryFacade.create(request.toCommand(userId))
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(DiaryDto.DiaryResponse.from(view))
    }

    @DeleteMapping("/{diaryId}")
    fun delete(
        @LoginUser userId: Long,
        @PathVariable diaryId: Long,
    ): ResponseEntity<Void> {
        diaryFacade.delete(diaryId, userId)
        return ResponseEntity.noContent().build()
    }
}
