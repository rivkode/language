package com.learner.language.domain.user.auth

data class CustomClaims(
    val userId: Long, val authorities: List<String>
) {
    companion object {
        fun of(userId: Long, authorities: List<String>): CustomClaims {
            return CustomClaims(userId, authorities)
        }
    }
}
