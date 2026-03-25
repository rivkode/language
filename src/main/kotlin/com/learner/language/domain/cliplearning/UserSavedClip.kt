package com.learner.language.domain.cliplearning

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "user_saved_clip",
    indexes = [
        Index(name = "uk_user_saved_clip_user_clip", columnList = "user_id, clip_id", unique = true),
        Index(name = "idx_user_saved_clip_user_id_created_at", columnList = "user_id, created_at"),
        Index(name = "idx_user_saved_clip_clip_id", columnList = "clip_id")
    ]
)
class UserSavedClip(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip_id", nullable = false)
    var clip: ClipLearningClip,
) : BaseEntity()
