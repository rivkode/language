package com.learner.language.domain

import jakarta.persistence.*

@Entity
@Table(name = "user")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(name = "label", nullable = false)
    var email: String,

    @Column(name = "label", nullable = false)
    var username: String,

    @Column(name = "label", nullable = false)
    var password: String,

    @Enumerated(EnumType.STRING)
    var role: Role

): BaseEntity() {

}

enum class Role {
        USER, ADMIN
}
