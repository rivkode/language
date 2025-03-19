package com.learner.language.domain.word

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class LevelConverter : AttributeConverter<CefrLevel, Int> {
    override fun convertToDatabaseColumn(attribute: CefrLevel?): Int {
        return attribute?.ordinal ?: 0
    }

    override fun convertToEntityAttribute(dbData: Int?): CefrLevel {
        return CefrLevel.entries.toTypedArray().getOrElse(dbData ?: 0) { CefrLevel.A1 }
    }
}
