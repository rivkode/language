package com.learner.language.infrastructure.chat.question

import com.learner.language.domain.chat.question.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QuestionRepository : JpaRepository<Question, Long>{
}
