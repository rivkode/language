package com.learner.language.domain.comment

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "diary_comment")
class DiaryComment(
    @Column(name = "diary_id", nullable = false)
    var diaryId: Long,

    @Column(name = "author_user_id", nullable = false)
    var authorUserId: Long,

    @Column(name = "text", nullable = false, length = MAX_TEXT_LENGTH)
    var text: String,

    @Column(name = "parent_comment_id")
    var parentCommentId: Long? = null,

    @Column(name = "like_count", nullable = false)
    var likeCount: Int = 0,
) : BaseEntity() {

    fun isAuthoredBy(userId: Long): Boolean = this.authorUserId == userId

    companion object {
        const val MAX_TEXT_LENGTH = 500
    }
}
