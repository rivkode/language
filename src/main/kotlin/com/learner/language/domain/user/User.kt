package com.learner.language.domain.user

import com.learner.language.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "user")
class User(

    @Column(name = "email", nullable = false)
    var email: String,

    @Column(name = "username", nullable = false)
    var username: String,

    @Column(name = "password", nullable = false)
    var password: String,

    @Enumerated(EnumType.STRING)
    var role: Role

): BaseEntity()

enum class Role {
    USER, ADMIN
}
