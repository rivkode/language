package com.learner.language.domain.diarychat

enum class DiaryChatMessageSource {
    USER,
    AI,
    SYSTEM;

    fun apiValue(): String = name.lowercase()

    companion object {
        fun fromApi(value: String): DiaryChatMessageSource =
            entries.firstOrNull { it.apiValue() == value.lowercase() }
                ?: throw IllegalArgumentException("Unknown source: $value")
    }
}
