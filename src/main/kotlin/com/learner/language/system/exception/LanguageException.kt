package com.learner.language.system.exception

open class LanguageException(
    private val errorCode: ErrorCode,
    message: String
) : RuntimeException(message){

    fun getErrorCode(): String {
        return errorCode.getValue()
    }
}