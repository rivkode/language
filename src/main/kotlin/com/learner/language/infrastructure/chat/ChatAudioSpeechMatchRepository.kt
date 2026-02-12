package com.learner.language.infrastructure.chat

import com.learner.language.domain.chat.ChatAudioSpeechMatch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatAudioSpeechMatchRepository : JpaRepository<ChatAudioSpeechMatch, Long> {

    @Query("SELECT * FROM chat_audio_speech_match WHERE chat_id in (:chatIds)", nativeQuery = true)
    fun findAllByChatIds(@Param("chatIds") chatIds: List<Long>): List<ChatAudioSpeechMatch>
}