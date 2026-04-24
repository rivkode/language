package com.learner.language.infrastructure.auth

import com.learner.language.application.auth.AuthCodeStore
import com.learner.language.domain.auth.AuthTokenSnapshot
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryAuthCodeStore(
    private val properties: AuthCodeProperties,
    private val clock: Clock = Clock.systemUTC(),
) : AuthCodeStore {

    private data class Entry(val snapshot: AuthTokenSnapshot, val expiresAt: Instant)

    private val store: ConcurrentHashMap<String, Entry> = ConcurrentHashMap()

    override fun issue(snapshot: AuthTokenSnapshot): String {
        val code = UUID.randomUUID().toString()
        val entry = Entry(snapshot, Instant.now(clock).plus(properties.ttl))
        store[code] = entry
        return code
    }

    override fun consume(code: String): AuthTokenSnapshot? {
        val entry = store.remove(code) ?: return null
        if (Instant.now(clock).isAfter(entry.expiresAt)) {
            return null
        }
        return entry.snapshot
    }

    @Scheduled(fixedDelayString = "PT30S")
    fun cleanupExpired() {
        val now = Instant.now(clock)
        store.entries.removeIf { now.isAfter(it.value.expiresAt) }
    }
}
