package com.learner.language.common.pagination

data class CursorPage<T>(
    val items: List<T>,
    val paging: PageInfo,
)

data class PageInfo(
    val nextCursor: String?,
    val hasNext: Boolean,
)
