package com.learner.language.domain.diary.exception

import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.ForbiddenException

class DiaryForbiddenException(message: String) : ForbiddenException(
    ErrorCode.DIARY_FORBIDDEN,
    message,
)
