package com.learner.language.infrastructure.chat.answer

import com.learner.language.domain.chat.answer.Answer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnswerRepository : JpaRepository<Answer, Long> {
}
