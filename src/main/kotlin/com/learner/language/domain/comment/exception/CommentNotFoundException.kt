package com.learner.language.domain.comment.exception

import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.NotFoundException

class CommentNotFoundException(message: String) : NotFoundException(
    ErrorCode.COMMENT_NOT_FOUND,
    message,
)
