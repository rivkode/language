package com.learner.language.infrastructure.sentence

import com.learner.language.domain.sentence.Sentence
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SentenceRepository : JpaRepository<Sentence, Long> {

}
