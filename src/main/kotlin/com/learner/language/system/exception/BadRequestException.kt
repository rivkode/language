package com.learner.language.system.exception

open class BadRequestException(
    errorCode: ErrorCode,
    message: String
) : LanguageException(errorCode, message) {
}