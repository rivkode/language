package com.learner.language.application.diarychat

import com.learner.language.application.diary.DiaryAuthorView
import com.learner.language.domain.user.UserReader
import org.springframework.stereotype.Component

@Component
class DiaryChatAuthorResolver(
    private val userReader: UserReader,
) {
    fun resolve(userIds: Collection<Long>): Map<Long, DiaryAuthorView> =
        userIds.distinct()
            .mapNotNull { id -> runCatching { userReader.getUserById(id) }.getOrNull()?.let { id to it } }
            .associate { (id, user) ->
                id to DiaryAuthorView(user.id, user.username, null)
            }

    fun aiAuthor(): DiaryAuthorView = DiaryAuthorView(AI_USER_ID, AI_USERNAME, null)

    companion object {
        const val AI_USER_ID = -1L
        const val AI_USERNAME = "Jamo AI"
    }
}
