package com.learner.language.domain.diarychat.exception

import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.ForbiddenException

class ChatroomForbiddenException(message: String) : ForbiddenException(
    ErrorCode.CHATROOM_FORBIDDEN,
    message,
)
