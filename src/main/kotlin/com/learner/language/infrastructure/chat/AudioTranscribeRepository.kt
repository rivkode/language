package com.learner.language.infrastructure.chat

import com.learner.language.domain.chat.AudioTranscribe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AudioTranscribeRepository : JpaRepository<AudioTranscribe, Long>{
}