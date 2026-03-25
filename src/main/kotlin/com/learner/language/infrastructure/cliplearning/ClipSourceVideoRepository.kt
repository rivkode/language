package com.learner.language.infrastructure.cliplearning

import com.learner.language.domain.cliplearning.ClipSourceVideo
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ClipSourceVideoRepository : JpaRepository<ClipSourceVideo, Long> {
    fun findByYoutubeVideoId(youtubeVideoId: String): Optional<ClipSourceVideo>
}
