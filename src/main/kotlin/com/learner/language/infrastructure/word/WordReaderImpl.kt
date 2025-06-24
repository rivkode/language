package com.learner.language.infrastructure.word

import com.learner.language.domain.user.User
import com.learner.language.domain.word.Part
import com.learner.language.domain.word.Word
import com.learner.language.domain.word.WordList
import com.learner.language.domain.word.WordReader
import com.learner.language.domain.word.review.WordReviewCount
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
        println(part)
        println(part.order)
        println(root.get<String>("part"))
        println(root.get<Int>("part"))

        predicates.add(cb.equal(root.get<Int>("part"), part.order))


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
        typedQuery.maxResults = 3

        return typedQuery.resultList
    }

    override fun getNoCountReviewWords(userId: Long, wordListId: Int): List<Word> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Word::class.java)
        val root = query.from(WordList::class.java)

        val wordJoin = root.join<WordList, Word>("word")

        val subquery = query.subquery(Long::class.java)
        val subRoot = subquery.from(WordReviewCount::class.java)
        subquery.select(subRoot.get<Word>("word").get<Long>("id"))
            .where(cb.equal(subRoot.get<User>("user").get<Long>("id"), userId))

        query.select(wordJoin).where(
            cb.equal(root.get<Int>("wordListId"), wordListId),
            cb.not(wordJoin.get<Long>("id").`in`(subquery))
        )

        val noCountWords = entityManager.createQuery(query)
        noCountWords.maxResults = 100

        return noCountWords.resultList
    }

    override fun getCountReviewWords(userId: Long, wordListId: Int, count: Int): List<Word> {
        val cb = entityManager.criteriaBuilder
        val query = cb.createQuery(Word::class.java)
        val root = query.from(WordList::class.java)
        val wordJoin = root.join<WordList, Word>("word")

        val subquery = query.subquery(Long::class.java)
        val subRoot = subquery.from(WordReviewCount::class.java)
        subquery.select(subRoot.get<Word>("word").get<Long>("id"))
            .where(
                cb.equal(subRoot.get<User>("user").get<Long>("id"), userId),
                cb.equal(subRoot.get<Int>("count"), count)
            )

        query.select(wordJoin).where(
            cb.equal(root.get<Int>("wordListId"), wordListId),
            wordJoin.get<Long>("id").`in`(subquery)
        )

        val noCountWords = entityManager.createQuery(query)
        noCountWords.maxResults = 100

        return noCountWords.resultList
    }

}
