package com.learner.language.domain.prompt

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class PersonaTypeConverter : AttributeConverter<PersonaType, Int> {
    override fun convertToDatabaseColumn(attribute: PersonaType?): Int {
        return attribute?.type ?: PersonaType.CHILD.type
    }

    override fun convertToEntityAttribute(dbData: Int): PersonaType {
        return PersonaType.entries.firstOrNull {it.type == dbData } ?: PersonaType.CHILD
    }
}
