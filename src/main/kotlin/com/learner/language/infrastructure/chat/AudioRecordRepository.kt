package com.learner.language.infrastructure.chat

import com.learner.language.domain.chat.AudioRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AudioRecordRepository : JpaRepository<AudioRecord, Long>{
}