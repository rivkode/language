package com.learner.language.interfaces.validation

import com.learner.language.application.validation.DiaryValidationResult
import com.learner.language.application.validation.LineValidation
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

object DiaryValidationDto {

    data class ValidateRequest(
        @field:NotNull
        @field:Size(min = 3, max = 3, message = "lines는 정확히 3개여야 합니다.")
        val lines: List<@NotEmpty String> = emptyList(),
    )

    data class ValidateLineRequest(
        @field:NotNull
        val lineIndex: Int,
        @field:NotEmpty
        val text: String,
    )

    data class LineResponse(
        val lineIndex: Int,
        val originalText: String,
        val status: String,
        val message: String,
        val suggestion: String?,
    ) {
        companion object {
            fun from(validation: LineValidation) = LineResponse(
                lineIndex = validation.lineIndex,
                originalText = validation.originalText,
                status = validation.status.apiValue(),
                message = validation.message,
                suggestion = validation.suggestion,
            )
        }
    }

    data class ValidationResponse(
        val validationId: String,
        val lines: List<LineResponse>,
    ) {
        companion object {
            fun from(result: DiaryValidationResult) = ValidationResponse(
                validationId = result.validationId,
                lines = result.lines.map { LineResponse.from(it) },
            )
        }
    }
}
