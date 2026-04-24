package com.learner.language.application.validation

data class DiaryValidationResult(
    val validationId: String,
    val lines: List<LineValidation>,
)
