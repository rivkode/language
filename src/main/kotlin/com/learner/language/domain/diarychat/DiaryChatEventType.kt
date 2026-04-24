package com.learner.language.domain.diarychat

enum class DiaryChatEventType {
    PARTICIPANT_JOINED,
    PARTICIPANT_LEFT,
    AI_TOGGLE_CHANGED,
    AI_TYPING,
    AI_FAILED;

    fun apiValue(): String = name.lowercase()
}
