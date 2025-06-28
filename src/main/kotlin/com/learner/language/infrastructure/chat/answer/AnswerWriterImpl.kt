package com.learner.language.infrastructure.chat.answer

import com.learner.language.domain.chat.answer.Answer
import com.learner.language.domain.chat.answer.AnswerWriter
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class AnswerWriterImpl(
    private val answerRepository: AnswerRepository
) : AnswerWriter {
    override fun save(answer: Answer): Answer {
        return answerRepository.save(answer)
    }

    @Async
    override fun asyncSave(answer: Answer): CompletableFuture<Answer> {
        return CompletableFuture.supplyAsync {
            answerRepository.save(answer)
        }
    }
}
