package com.learner.language.infrastructure.chat.question

import com.learner.language.domain.chat.question.Question
import com.learner.language.domain.chat.question.QuestionWriter
import org.springframework.stereotype.Component

@Component
class QuestionWriterImpl(
    private val questionRepository: QuestionRepository
) : QuestionWriter {
    override fun save(question: Question): Question {
        return questionRepository.save(question)
    }

}
