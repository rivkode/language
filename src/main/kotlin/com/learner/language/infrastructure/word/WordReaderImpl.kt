package com.learner.language.infrastructure.word

import com.learner.language.domain.word.Part
import com.learner.language.domain.word.Word
import com.learner.language.domain.word.WordReader
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.Predicate
import org.springframework.stereotype.Component

@Component
class WordReaderImpl(
    private val wordRepository: WordRepository,
    private val entityManager: EntityManager
) : WordReader {
    override fun getWordListByIds(ids: List<Long>) : List<Word> {
        return wordRepository.findAllById(ids).toList()
    }

    override fun getWordById(id: Long) : Word{
        return wordRepository.findById(id).get()
    }

    override fun getChoiceWord(part: Part, wordIds: List<Long>, lastId: Long?, pageSize: Int): List<Word> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Word::class.java)
        val root = query.from(Word::class.java)

        val predicates = mutableListOf<Predicate>()

        predicates.add(cb.equal(root.get<String>("part"), part))

        if (wordIds.isNotEmpty()) {
            predicates.add(root.get<Long>("id").`in`(wordIds).not())
        }

        // **Keyset Pagination 적용**: lastId가 있을 경우 WHERE id > lastId
        if (lastId != null) {
            predicates.add(cb.greaterThan(root.get("id"), lastId))
        }

        query.select(root).where(*predicates.toTypedArray())
            .orderBy(cb.asc(root.get<Long>("id")))

        val typedQuery = entityManager.createQuery(query)
        if (wordIds.isEmpty()) {
            typedQuery.maxResults = 3
        }

        return typedQuery.resultList
    }
}
