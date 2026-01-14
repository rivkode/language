package com.learner.language.infrastructure.feedback

import com.learner.language.domain.feedback.Feedback
import com.learner.language.domain.feedback.FeedbackReader
import com.learner.language.domain.sentence.Sentence
import com.learner.language.domain.user.User
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component

@Component
class FeedbackReaderImpl(
    private val entityManager: EntityManager
): FeedbackReader {
    override fun getUserFeedback(userId: Long, sentenceId: Long): Feedback {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Feedback::class.java)
        val root = query.from(Feedback::class.java)

        val predicates = mutableListOf<Predicate>()

        predicates.add(cb.equal(root.get<User>("user").get<Long>("id"), userId))
        predicates.add(cb.equal(root.get<Sentence>("sentence").get<Long>("id"), sentenceId))

        query.select(root).where(*predicates.toTypedArray())

        val typedQuery = entityManager.createQuery(query)

        return typedQuery.singleResult
    }
}
