package com.learner.language.domain.diarychat.exception

import com.learner.language.system.exception.BadRequestException
import com.learner.language.system.exception.ErrorCode

class ChatroomMessageTooLongException(message: String) : BadRequestException(
    ErrorCode.CHATROOM_MESSAGE_TOO_LONG,
    message,
)
