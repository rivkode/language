package com.learner.language.infrastructure.user

import com.learner.language.domain.user.User
import com.learner.language.domain.user.UserEmail
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CrudRepository<User, Long> {
    fun findByEmail(userEmail: UserEmail): Optional<User>
    fun existsByEmail(userEmail: UserEmail): Boolean
}
