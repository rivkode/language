package com.learner.language.infrastructure.shorts

import com.learner.language.domain.shorts.Shorts
import org.springframework.data.jpa.repository.JpaRepository

interface ShortsRepository : JpaRepository<Shorts, Long>
