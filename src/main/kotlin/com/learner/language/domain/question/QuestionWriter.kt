package com.learner.language.domain.question

interface QuestionWriter {
    fun save(question: Question) : Question
}
