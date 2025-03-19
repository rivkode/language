package com.learner.language.infrastructure.word

import com.learner.language.domain.word.Word
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WordRepository : JpaRepository<Word, Long> {

}
