package com.learner.language.application.diarychat

import com.learner.language.infrastructure.diarychat.DiaryChatMessageRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class DiaryChatRetentionScheduler(
    private val messageRepository: DiaryChatMessageRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 4 * * *", zone = "UTC")
    @Transactional
    fun pruneOldMessages() {
        val cutoff = LocalDateTime.now().minusDays(RETENTION_DAYS)
        val removed = messageRepository.deleteOlderThan(cutoff)
        if (removed > 0) {
            log.info("DiaryChat retention prune removed {} messages older than {}", removed, cutoff)
        }
    }

    companion object {
        private const val RETENTION_DAYS = 90L
    }
}
