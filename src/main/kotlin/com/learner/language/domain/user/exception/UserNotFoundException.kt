package com.learner.language.domain.user.exception

import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.NotFoundException

class UserNotFoundException(
    message: String
) : NotFoundException(ErrorCode.NOT_FOUND, message) {
}