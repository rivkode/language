package com.learner.language.system.exception

class ServiceUnavailableException(
    errorCode: ErrorCode,
    message: String
) : LanguageException(errorCode, message)
