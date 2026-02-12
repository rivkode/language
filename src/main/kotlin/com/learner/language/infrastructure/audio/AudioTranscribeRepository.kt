package com.learner.language.infrastructure.audio

import com.learner.language.domain.audio.AudioTranscribe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AudioTranscribeRepository : JpaRepository<AudioTranscribe, Long>{
}