package com.learner.language.infrastructure.question

import com.learner.language.domain.question.Question
import com.learner.language.domain.question.QuestionReader
import com.learner.language.domain.user.User
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component

@Component
class QuestionReaderImpl(
    private val questionRepository: QuestionRepository,
    private val entityManager: EntityManager
) : QuestionReader {
    override fun getQuestionListByUserId(userId: Long) : List<Question> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Question::class.java)
        val root = query.from(Question::class.java)

        val predicates = mutableListOf<Predicate>()

        predicates.add(cb.equal(root.get<User>("user").get<Long>("id"), userId))

        query.select(root).where(*predicates.toTypedArray())
            .orderBy(cb.desc(root.get<Long>("id")))

        val typedQuery = entityManager.createQuery(query)

        return typedQuery.resultList ?: emptyList()
    }

    override fun getUserQuestion(userId: Long, questionId: Long): Question {
        return questionRepository.findById(questionId).get()
    }
}
