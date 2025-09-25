package com.learner.language.domain.word

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class PartConverter : AttributeConverter<Part, Int> {
    override fun convertToDatabaseColumn(attribute: Part?): Int {
        return attribute?.order ?: Part.NOUN.order
    }

    override fun convertToEntityAttribute(dbData: Int?): Part {
        return Part.entries.firstOrNull { it.order == dbData } ?: Part.NOUN
    }

}
