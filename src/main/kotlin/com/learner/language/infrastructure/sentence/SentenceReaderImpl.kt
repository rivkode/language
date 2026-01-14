package com.learner.language.infrastructure.sentence

import com.learner.language.domain.sentence.Sentence
import com.learner.language.domain.sentence.SentenceReader
import com.learner.language.domain.user.User
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component

@Component
class SentenceReaderImpl(
    private val sentenceRepository: SentenceRepository,
    private val entityManager: EntityManager
) : SentenceReader {
    override fun getSentenceById(id: Long): Sentence {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Sentence::class.java)
        val root = query.from(Sentence::class.java)

        val predicates = mutableListOf<Predicate>()

        predicates.add(cb.equal(root.get<Long>("id"), id))

        query.select(root).where(*predicates.toTypedArray())

        val typedQuery = entityManager.createQuery(query)

        return typedQuery.singleResult
    }

    override fun getSentenceListByUserId(userId: Long): List<Sentence> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Sentence::class.java)
        val root = query.from(Sentence::class.java)

        val predicates = mutableListOf<Predicate>()

        predicates.add(cb.equal(root.get<User>("user").get<Long>("id"), userId))

        query.select(root).where(*predicates.toTypedArray())
            .orderBy(cb.desc(root.get<Long>("id")))

        val typedQuery = entityManager.createQuery(query)

        return typedQuery.resultList ?: emptyList()
    }

    override fun getUserSentence(userId: Long, sentenceId: Long): Sentence {
        return sentenceRepository.findById(sentenceId).get()
    }

}
