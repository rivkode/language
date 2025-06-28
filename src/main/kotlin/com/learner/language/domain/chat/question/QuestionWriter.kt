package com.learner.language.domain.chat.question

interface QuestionWriter {
    fun save(question: Question) : Question
}
