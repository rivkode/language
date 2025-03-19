package com.learner.language.infrastructure.word.wordusermatch

import com.learner.language.domain.word.usermatch.WordUserMatch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface WordUserMatchRepository : JpaRepository<WordUserMatch, Long> {

    @Query("SELECT wum.word.id FROM WordUserMatch wum WHERE wum.user.id = :userId")
    fun findWordIdsByUserId(@Param("userId") userId: Long) : List<Long>

}
