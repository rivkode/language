package com.learner.language.domain.chat

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ChatContextTypeConverter : AttributeConverter<ChatContextType, String> {
    override fun convertToDatabaseColumn(attribute: ChatContextType?): String {
        return attribute?.code ?: ChatContextType.GENERAL.code
    }

    override fun convertToEntityAttribute(dbData: String?): ChatContextType {
        return ChatContextType.entries.firstOrNull { it.code == dbData } ?: ChatContextType.GENERAL
    }
}
