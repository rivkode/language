package com.learner.language.domain.user.auth

import com.learner.language.system.exception.BadRequestException
import com.learner.language.system.exception.ErrorCode

class AuthAuthenticationException(
    message: String
): BadRequestException(ErrorCode.BAD_REQUEST, message) {
}