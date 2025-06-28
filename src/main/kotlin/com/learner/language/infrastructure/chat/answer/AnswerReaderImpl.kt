package com.learner.language.infrastructure.chat.answer

import com.learner.language.domain.chat.answer.Answer
import com.learner.language.domain.chat.answer.AnswerReader
import com.learner.language.domain.chat.question.Question
import com.learner.language.domain.user.User
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component

@Component
class AnswerReaderImpl(
    private val entityManager: EntityManager
) : AnswerReader {
    override fun getUserAnswer(userId: Long, questionId: Long): Answer {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Answer::class.java)
        val root = query.from(Answer::class.java)

        val predicates = mutableListOf<Predicate>()

        predicates.add(cb.equal(root.get<User>("user").get<Long>("id"), userId))
        predicates.add(cb.equal(root.get<Question>("question").get<Long>("id"), questionId))

        query.select(root).where(*predicates.toTypedArray())

        val typedQuery = entityManager.createQuery(query)

        return typedQuery.singleResult
    }

}
