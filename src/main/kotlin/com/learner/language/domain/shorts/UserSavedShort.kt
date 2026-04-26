package com.learner.language.domain.shorts

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
    name = "user_saved_short",
    indexes = [
        Index(name = "uk_user_saved_short_user_short", columnList = "user_id, short_id", unique = true),
        Index(name = "idx_user_saved_short_user_id_created_at", columnList = "user_id, created_at"),
        Index(name = "idx_user_saved_short_short_id", columnList = "short_id"),
    ]
)
class UserSavedShort(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "short_id", nullable = false)
    var short: Shorts,
) : BaseEntity()
