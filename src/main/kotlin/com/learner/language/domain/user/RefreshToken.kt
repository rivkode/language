package com.learner.language.domain.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.util.UUID

@Embeddable
data class RefreshToken(
    @Column(name = "refresh_token")
    var refreshToken: String = UUID.randomUUID().toString()
) {
}