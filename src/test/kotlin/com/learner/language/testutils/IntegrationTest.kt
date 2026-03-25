package com.learner.language.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.learner.language.testutils.config.TestUtilConfig
import com.learner.language.testutils.persistence.SentencePersistenceUtils
import com.learner.language.testutils.persistence.ClipLearningPersistenceUtils
import com.learner.language.testutils.persistence.ProfilePersistenceUtils
import com.learner.language.testutils.persistence.UserPersistenceUtils
import com.learner.language.testutils.persistence.WordPersistenceUtils
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@Import(TestUtilConfig::class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class IntegrationTest : BehaviorSpec() {
    override fun extensions(): List<Extension> = listOf(SpringExtension)

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var userPersistenceUtils: UserPersistenceUtils

    @Autowired
    protected lateinit var wordPersistenceUtils: WordPersistenceUtils

    @Autowired
    protected lateinit var sentencePersistenceUtils: SentencePersistenceUtils

    @Autowired
    protected lateinit var profilePersistenceUtils: ProfilePersistenceUtils

    @Autowired
    protected lateinit var clipLearningPersistenceUtils: ClipLearningPersistenceUtils

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
    }

    override suspend fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
    }

}
