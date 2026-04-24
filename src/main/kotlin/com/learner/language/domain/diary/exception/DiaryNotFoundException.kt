package com.learner.language.domain.diary.exception

import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.NotFoundException

class DiaryNotFoundException(message: String) : NotFoundException(
    ErrorCode.DIARY_NOT_FOUND,
    message,
)
