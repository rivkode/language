package com.learner.language.domain.prompt

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class PersonaType(
    val type: Int
) {
    CHILD(1), FRIEND(2), COWORKER(3), TEACHER(4);

    companion object {

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun from(value: Any): PersonaType {
            return when (value) {
                is Int -> entries.find { it.type == value }
                is String -> entries.find { it.name == value }
                else -> null
            } ?: throw IllegalArgumentException("Invalid PersonaType: $value")
        }

        @JsonValue
        @JvmStatic
        fun toJson(personaType: PersonaType): String {
            return personaType.name
        }
    }
}
