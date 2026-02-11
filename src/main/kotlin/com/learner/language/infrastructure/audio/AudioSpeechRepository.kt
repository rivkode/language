package com.learner.language.infrastructure.audio

import com.learner.language.domain.audio.AudioSpeech
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AudioSpeechRepository : JpaRepository<AudioSpeech, Long>{

    @Query("SELECT a.* " +
            "FROM audio_speech a " +
            "JOIN chat_audio_speech_match casm on casm.audio_speech_id = a.id " +
            "WHERE casm.chat_id in (:chatIds)"
        , nativeQuery = true)
    fun findAllByChatIds(@Param("chatIds") chatIds: List<Long>): List<AudioSpeech>
}