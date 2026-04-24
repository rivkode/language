package com.learner.language.system.exception

open class LanguageException(
    private val errorCode: ErrorCode,
    message: String,
    open val publicCode: String = errorCode.name,
) : RuntimeException(message) {

    fun getErrorCode(): String {
        return errorCode.getValue()
    }
}
