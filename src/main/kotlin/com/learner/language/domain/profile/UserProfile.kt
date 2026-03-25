package com.learner.language.domain.profile

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "user_profile",
    indexes = [
        Index(name = "uk_user_profile_user_id", columnList = "user_id", unique = true)
    ]
)
class UserProfile(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    var user: User,

    @Column(name = "display_name", nullable = false, length = 30)
    var displayName: String,

    @Column(name = "bio", length = 160)
    var bio: String? = null,

    @Column(name = "profile_image_url", length = 1000)
    var profileImageUrl: String? = null,
) : BaseEntity()
