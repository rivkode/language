package com.learner.language.domain.word.match

import com.learner.language.domain.user.User
import com.learner.language.domain.word.Word
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface WordUserMatchRepository : JpaRepository<WordUserMatch, Long> {

    @Query("SELECT wum.id FROM WordUserMatch wum WHERE wum.user.id = :userId")
    fun findWordIdsByUserId(@Param("userId") userId: Long) : List<Long>

}
