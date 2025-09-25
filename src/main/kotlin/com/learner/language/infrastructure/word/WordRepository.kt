package com.learner.language.infrastructure.word

import com.learner.language.domain.word.Word
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface WordRepository : JpaRepository<Word, Long> {

}
