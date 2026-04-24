package com.learner.language.domain.diary.exception

import com.learner.language.system.exception.BadRequestException
import com.learner.language.system.exception.ErrorCode

class InvalidLineLengthException(message: String) : BadRequestException(
    ErrorCode.INVALID_LINE_LENGTH,
    message,
)
