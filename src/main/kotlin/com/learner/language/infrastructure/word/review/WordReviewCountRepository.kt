package com.learner.language.infrastructure.word.review

import com.learner.language.domain.word.review.WordReviewCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface WordReviewCountRepository : JpaRepository<WordReviewCount, Long> {

    @Query(nativeQuery = true, value = """
        SELECT *
        FROM word_review_count
        WHERE user_id = :userId AND word_id = :wordId
    """)
    fun getByUserIdAndWordId(@Param("userId") userId: Long, @Param("wordId") wordId: Long): WordReviewCount?
}