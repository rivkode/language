package com.learner.language.application.diarychat

import org.springframework.stereotype.Component
import org.springframework.web.context.request.async.DeferredResult
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Component
class DiaryChatPollingHub {

    private data class Subscription(
        val after: Long,
        val deferred: DeferredResult<DiaryChatPollView>,
    )

    private val subscribers: ConcurrentHashMap<Long, CopyOnWriteArrayList<Subscription>> =
        ConcurrentHashMap()

    fun subscribe(roomId: Long, after: Long, deferred: DeferredResult<DiaryChatPollView>) {
        val list = subscribers.computeIfAbsent(roomId) { CopyOnWriteArrayList() }
        val sub = Subscription(after, deferred)
        list.add(sub)
        deferred.onCompletion { list.remove(sub) }
        deferred.onTimeout { list.remove(sub) }
    }

    fun notifyRoom(roomId: Long, supplier: (Long) -> DiaryChatPollView) {
        val list = subscribers[roomId] ?: return
        val toRemove = mutableListOf<Subscription>()
        list.forEach { sub ->
            val view = supplier(sub.after)
            if (view.items.isNotEmpty() || view.events.isNotEmpty()) {
                if (sub.deferred.setResult(view)) toRemove += sub
            }
        }
        toRemove.forEach { list.remove(it) }
    }
}
