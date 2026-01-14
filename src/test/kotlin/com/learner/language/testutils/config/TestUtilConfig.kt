package com.learner.language.testutils.config

import com.learner.language.infrastructure.sentence.SentenceRepository
import com.learner.language.infrastructure.user.UserRepository
import com.learner.language.infrastructure.word.WordRepository
import com.learner.language.system.security.CustomPasswordEncoder
import com.learner.language.testutils.persistence.SentencePersistenceUtils
import com.learner.language.testutils.persistence.UserPersistenceUtils
import com.learner.language.testutils.persistence.WordPersistenceUtils
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestUtilConfig {

    @Bean
    fun userTestUtils(
        userRepository: UserRepository,
        passwordEncoder: CustomPasswordEncoder
    ): UserPersistenceUtils {
        return UserPersistenceUtils(userRepository, passwordEncoder)
    }

    @Bean
    fun wordTestUtils(
        wordRepository: WordRepository
    ): WordPersistenceUtils {
        return WordPersistenceUtils(wordRepository)
    }

    @Bean
    fun sentenceTestUtils(
        sentenceRepository: SentenceRepository
    ): SentencePersistenceUtils {
        return SentencePersistenceUtils(sentenceRepository)
    }
}
