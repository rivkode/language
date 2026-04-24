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
    var password: UserPassword? = null,

    @Enumerated(EnumType.STRING)
    var role: Role,

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    var provider: AuthProvider = AuthProvider.LOCAL,

    @Column(name = "provider_external_id", length = 100)
    var providerExternalId: String? = null,

): BaseEntity() {

    fun updateUsername(newUsername: String) {
        if (newUsername.isNotBlank() && newUsername != this.username) {
            this.username = newUsername
        }
    }

    companion object {
        fun ofOAuth(
            email: String,
            username: String,
            provider: AuthProvider,
            providerExternalId: String,
        ): User = User(
            email = UserEmail(email),
            username = username,
            password = null,
            role = Role.USER,
            provider = provider,
            providerExternalId = providerExternalId,
        )
    }
}

enum class Role {
    USER, ADMIN
}
