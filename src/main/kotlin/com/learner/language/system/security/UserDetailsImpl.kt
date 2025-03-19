package com.learner.language.system.security

import com.learner.language.domain.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(
    private val user: User
) : UserDetails {

    fun getUser(): User = user

    fun getEmail(): String = user.email.email

    fun getUserId(): Long = user.id
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authority = "ROLE_USER"
        return listOf(SimpleGrantedAuthority(authority))
    }

    override fun getPassword(): String = user.password.password

    override fun getUsername(): String = user.username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
