package com.learner.language.domain.comment.exception

import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.ForbiddenException

class CommentForbiddenException(message: String) : ForbiddenException(
    ErrorCode.COMMENT_FORBIDDEN,
    message,
)
