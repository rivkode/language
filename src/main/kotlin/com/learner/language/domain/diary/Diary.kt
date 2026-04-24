package com.learner.language.domain.diary

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "diary")
class Diary(
    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "line_1", nullable = false, length = LINE_MAX_LENGTH)
    var line1: String,

    @Column(name = "line_2", nullable = false, length = LINE_MAX_LENGTH)
    var line2: String,

    @Column(name = "line_3", nullable = false, length = LINE_MAX_LENGTH)
    var line3: String,

    @Column(name = "is_public", nullable = false)
    var isPublic: Boolean = true,

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,

    @Column(name = "comment_count", nullable = false)
    var commentCount: Int = 0,
) : BaseEntity() {

    fun lines(): List<String> = listOf(line1, line2, line3)

    fun isOwnedBy(userId: Long): Boolean = this.userId == userId

    companion object {
        const val LINE_COUNT = 3
        const val LINE_MAX_LENGTH = 200
        const val TAG_MAX_COUNT = 5
        const val TAG_MAX_LENGTH = 32
    }
}
