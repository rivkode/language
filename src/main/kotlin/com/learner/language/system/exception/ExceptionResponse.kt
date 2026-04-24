package com.learner.language.system.exception

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ExceptionResponse(
    val message: String,
    val code: String? = null,
)
