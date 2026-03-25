package com.learner.language.infrastructure.cliplearning

import com.learner.language.domain.cliplearning.ClipLearningSentence
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ClipLearningSentenceRepository : JpaRepository<ClipLearningSentence, Long> {
    fun findByClipId(clipId: Long): Optional<ClipLearningSentence>
}
