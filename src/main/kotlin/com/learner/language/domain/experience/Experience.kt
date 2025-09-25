package com.learner.language.domain.experience

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "experience")
class Experience(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(name = "point")
    val point: Int

    ) : BaseEntity() {
}
