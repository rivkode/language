package com.learner.language.infrastructure.feedback

import com.learner.language.domain.feedback.Feedback
import com.learner.language.domain.feedback.FeedbackWriter
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class FeedbackWriterImpl(
    private val feedbackRepository: FeedbackRepository
) : FeedbackWriter {
    override fun save(feedback: Feedback): Feedback {
        return feedbackRepository.save(feedback)
    }

    @Async
    override fun asyncSave(feedback: Feedback): CompletableFuture<Feedback> {
        return CompletableFuture.supplyAsync {
            feedbackRepository.save(feedback)
        }
    }

}
