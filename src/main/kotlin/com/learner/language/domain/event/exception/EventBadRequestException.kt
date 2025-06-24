package com.learner.language.domain.event.exception

import com.learner.language.system.exception.BadRequestException
import com.learner.language.system.exception.ErrorCode

class EventBadRequestException(
    message: String
) : BadRequestException(ErrorCode.BAD_REQUEST, message){
}