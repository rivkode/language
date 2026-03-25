package com.learner.language.domain.cliplearning

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "user_clip_learning_progress",
    indexes = [
        Index(name = "uk_user_clip_progress_user_clip", columnList = "user_id, clip_id", unique = true),
        Index(name = "idx_user_clip_progress_user_id_updated_at", columnList = "user_id, updated_at"),
        Index(name = "idx_user_clip_progress_clip_id", columnList = "clip_id")
    ]
)
class UserClipLearningProgress(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip_id", nullable = false)
    var clip: ClipLearningClip,

    @Column(name = "last_viewed_position_ms", nullable = false)
    var lastViewedPositionMs: Long = 0L,

    @Column(name = "repeat_enabled", nullable = false)
    var repeatEnabled: Boolean = false,

    @Column(name = "translation_visible", nullable = false)
    var translationVisible: Boolean = false,

    @Column(name = "completed", nullable = false)
    var completed: Boolean = false,
) : BaseEntity()
