package com.learner.language.infrastructure.feedback

import com.learner.language.domain.feedback.Feedback
import org.springframework.data.jpa.repository.JpaRepository

interface FeedbackRepository : JpaRepository<Feedback, Long> {
}
