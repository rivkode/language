package com.learner.language.domain.user

import com.learner.language.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "\"user\"")
class User(

    @Embedded
    var email: UserEmail,

    @Column(name = "username", nullable = false)
    var username: String,

    @Embedded
    var password: UserPassword,

    @Enumerated(EnumType.STRING)
    var role: Role

): BaseEntity()

enum class Role {
    USER, ADMIN
}
