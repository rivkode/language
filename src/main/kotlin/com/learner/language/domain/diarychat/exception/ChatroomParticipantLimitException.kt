package com.learner.language.domain.diarychat.exception

import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.LanguageException

class ChatroomParticipantLimitException(message: String) : LanguageException(
    ErrorCode.CHATROOM_PARTICIPANT_LIMIT,
    message,
    publicCode = ErrorCode.CHATROOM_PARTICIPANT_LIMIT.name,
)
