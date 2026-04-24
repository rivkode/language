package com.learner.language.domain.comment

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "comment_like")
class CommentLike(
    @Column(name = "comment_id", nullable = false)
    var commentId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long,
) : BaseEntity()
