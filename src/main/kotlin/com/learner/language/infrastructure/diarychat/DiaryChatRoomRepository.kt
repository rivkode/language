package com.learner.language.infrastructure.diarychat

import com.learner.language.domain.diarychat.DiaryChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface DiaryChatRoomRepository : JpaRepository<DiaryChatRoom, Long> {
    fun findByDiaryId(diaryId: Long): Optional<DiaryChatRoom>

    @Modifying
    @Query("UPDATE DiaryChatRoom r SET r.participantCount = r.participantCount + 1 WHERE r.id = :id")
    fun incrementParticipantCount(@Param("id") id: Long): Int

    @Modifying
    @Query("UPDATE DiaryChatRoom r SET r.participantCount = r.participantCount - 1 WHERE r.id = :id AND r.participantCount > 0")
    fun decrementParticipantCount(@Param("id") id: Long): Int
}
