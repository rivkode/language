package com.learner.language

import com.learner.language.testutils.config.TestUtilConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@Import(TestUtilConfig::class)
@ActiveProfiles("test")
class LanguageApplicationTests {

    @Test
    fun contextLoads() {
    }

}
