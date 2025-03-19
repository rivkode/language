package com.learner.language.infrastructure.question

import com.learner.language.domain.question.Question
import com.learner.language.domain.question.QuestionWriter
import org.springframework.stereotype.Component

@Component
class QuestionWriterImpl(
    private val questionRepository: QuestionRepository
) : QuestionWriter {
    override fun save(question: Question): Question {
        return questionRepository.save(question)
    }

}
