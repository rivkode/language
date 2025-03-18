package com.learner.language.domain.user

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "user_refresh_token")
class UserRefreshToken(
    @Column(name = "user_id")
    var userId: Long,

    @Embedded
    var refreshToken: RefreshToken = RefreshToken()

): BaseEntity() {

    fun updateRefreshToken() {
        this.refreshToken = RefreshToken()
    }
}
