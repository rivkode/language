package com.learner.language.domain.user.exception

import com.learner.language.system.exception.BadRequestException
import com.learner.language.system.exception.ErrorCode

class UserBadRequestException(
    message: String
) : BadRequestException(ErrorCode.BAD_REQUEST, message){
}