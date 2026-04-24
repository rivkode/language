package com.learner.language.common.pagination

import java.time.Instant
import java.util.Base64

object CursorCodec {

    data class Cursor(val createdAt: Instant, val id: Long)

    fun encode(createdAt: Instant, id: Long): String {
        val raw = "${createdAt.toEpochMilli()}:$id"
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.toByteArray())
    }

    fun decode(cursor: String?): Cursor? {
        if (cursor.isNullOrBlank()) return null
        return runCatching {
            val decoded = String(Base64.getUrlDecoder().decode(cursor))
            val parts = decoded.split(":")
            require(parts.size == 2)
            Cursor(Instant.ofEpochMilli(parts[0].toLong()), parts[1].toLong())
        }.getOrNull()
    }
}
