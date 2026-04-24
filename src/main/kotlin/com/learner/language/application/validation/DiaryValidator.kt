package com.learner.language.application.validation

interface DiaryValidator {
    fun validate(lines: List<String>): List<LineValidation>
    fun validateLine(lineIndex: Int, text: String): LineValidation
}

data class LineValidation(
    val lineIndex: Int,
    val originalText: String,
    val status: LineStatus,
    val message: String,
    val suggestion: String?,
)

enum class LineStatus {
    OK, SUGGESTION, ERROR;

    fun apiValue(): String = name.lowercase()

    companion object {
        fun fromApi(value: String?): LineStatus = when (value?.lowercase()) {
            "ok" -> OK
            "suggestion" -> SUGGESTION
            "error" -> ERROR
            else -> ERROR
        }
    }
}
