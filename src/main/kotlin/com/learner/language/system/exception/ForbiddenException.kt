package com.learner.language.system.exception

open class ForbiddenException(
    errorCode: ErrorCode,
    message: String,
    publicCode: String = errorCode.name,
) : LanguageException(errorCode, message, publicCode)
