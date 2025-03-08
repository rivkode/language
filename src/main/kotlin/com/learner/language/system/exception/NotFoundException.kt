package com.learner.language.system.exception

open class NotFoundException(
    errorCode: ErrorCode,
    message: String
) : LanguageException(errorCode, message) {
}