package com.learner.language.interfaces.diary

import com.learner.language.application.diary.DiaryLikeFacade
import com.learner.language.system.login.LoginUser
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/diaries")
class DiaryLikeController(
    private val diaryLikeFacade: DiaryLikeFacade,
) {

    @PostMapping("/{diaryId}/like")
    fun toggle(
        @LoginUser userId: Long,
        @PathVariable diaryId: Long,
        @Valid @RequestBody request: DiaryLikeDto.ToggleRequest,
    ): ResponseEntity<DiaryLikeDto.ToggleResponse> {
        val result = diaryLikeFacade.setLiked(diaryId, userId, request.liked)
        return ResponseEntity.ok(DiaryLikeDto.ToggleResponse.from(result))
    }
}
