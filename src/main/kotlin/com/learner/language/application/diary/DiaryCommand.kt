package com.learner.language.application.diary

data class DiaryCreateCommand(
    val authorUserId: Long,
    val lines: List<String>,
    val tags: List<String>,
    val isPublic: Boolean,
)

data class DiaryFeedQuery(
    val cursor: String?,
    val size: Int,
    val sort: DiaryFeedSort,
    val tag: String?,
)

enum class DiaryFeedSort {
    RECENT,
    TRENDING;

    companion object {
        fun from(value: String?): DiaryFeedSort = when (value?.lowercase()) {
            "trending" -> TRENDING
            null, "recent" -> RECENT
            else -> RECENT
        }
    }
}
