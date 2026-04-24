package com.learner.language.system.exception

open class UnprocessableEntityException(
    errorCode: ErrorCode,
    message: String,
    publicCode: String = errorCode.name,
) : LanguageException(errorCode, message, publicCode)
