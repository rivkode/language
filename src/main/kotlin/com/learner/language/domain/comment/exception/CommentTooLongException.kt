package com.learner.language.domain.comment.exception

import com.learner.language.system.exception.BadRequestException
import com.learner.language.system.exception.ErrorCode

class CommentTooLongException(message: String) : BadRequestException(
    ErrorCode.COMMENT_TOO_LONG,
    message,
)
