package com.learner.language.domain.diarychat.exception

import com.learner.language.system.exception.ErrorCode
import com.learner.language.system.exception.LanguageException

class PollCursorExpiredException(message: String) : LanguageException(
    ErrorCode.POLL_CURSOR_EXPIRED,
    message,
    publicCode = ErrorCode.POLL_CURSOR_EXPIRED.name,
)
