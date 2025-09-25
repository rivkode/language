package com.learner.language.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.random.RandomGenerator

import kotlin.random.Random


@Component
class RandomNumber {

    fun generateRandomNumber(): String {
        val generator = RandomGenerator.of("L128X256MixRandom")
        val randomNumbers = List(6) { generator.nextInt(10).toString() }.joinToString("")

        return randomNumbers
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(RandomNumber::class.java)
    }

    fun <T> List<T>.randomSubset(count: Int): List<T> {
        if (count <= 0) {
            return emptyList()
        }
        if (count >= this.size) {
            return this.shuffled()
        }

        val random = Random.Default
        val indices = this.indices.shuffled(random).take(count).sorted()
        return indices.map { this[it] }
    }
}
