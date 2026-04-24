package com.learner.language.domain.diary.exception

import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.UnprocessableEntityException

class InvalidLineCountException(message: String) : UnprocessableEntityException(
    ErrorCode.INVALID_LINE_COUNT,
    message,
)
