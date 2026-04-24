package com.learner.language.application.diarychat

import org.springframework.stereotype.Component
import org.springframework.web.context.request.async.DeferredResult
import java.util.concurrent.ConcurrentHashMap

@Component
class DiaryChatPollingHub {

    private val subscribers: ConcurrentHashMap<Long, MutableList<DeferredResult<DiaryChatPollView>>> =
        ConcurrentHashMap()

    fun subscribe(roomId: Long, deferred: DeferredResult<DiaryChatPollView>) {
        subscribers
            .computeIfAbsent(roomId) { java.util.Collections.synchronizedList(mutableListOf()) }
            .add(deferred)
    }

    fun unsubscribe(roomId: Long, deferred: DeferredResult<DiaryChatPollView>) {
        subscribers[roomId]?.remove(deferred)
    }

    fun notifyRoom(roomId: Long, resultSupplier: () -> DiaryChatPollView?) {
        val waiters = subscribers[roomId] ?: return
        val snapshot = synchronized(waiters) { waiters.toList() }
        snapshot.forEach { deferred ->
            val payload = resultSupplier() ?: return@forEach
            if (payload.items.isEmpty() && payload.events.isEmpty()) return@forEach
            if (deferred.setResult(payload)) {
                waiters.remove(deferred)
            }
        }
    }
}
