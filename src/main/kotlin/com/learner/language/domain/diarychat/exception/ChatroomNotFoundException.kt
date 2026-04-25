package com.learner.language.domain.diarychat.exception

import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.NotFoundException

class ChatroomNotFoundException(message: String) : NotFoundException(
    ErrorCode.CHATROOM_NOT_FOUND,
    message,
)
