package com.learner.language.infrastructure.sentence.sentencewordmatch

import com.learner.language.domain.sentence.wordmatch.SentenceWordMatch
import com.learner.language.domain.word.Word
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SentenceWordMatchRepository : JpaRepository<SentenceWordMatch, Long> {

    @Query("select w.*\n" +
        "from sentence_word_match swm\n" +
        "join word w on w.id = swm.word_id\n" +
        "where swm.sentence_id = :sentenceId", nativeQuery = true)
    fun findWordIdsById(@Param("sentenceId") sentenceId: Long) : List<Word>
}