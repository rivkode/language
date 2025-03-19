package com.learner.language.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.random.RandomGenerator

@Component
class RandomNumber {

    fun generateRandomNumber(): String {
        val generator = RandomGenerator.of("L128X256MixRandom")
        val randomNumbers = List(6) { generator.nextInt(10).toString() }.joinToString("")

        log.info("random : $randomNumbers")

        return randomNumbers
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(RandomNumber::class.java)
    }
}
