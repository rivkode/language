package com.learner.language.utils

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisUtil(
    private val redisTemplate: StringRedisTemplate
) {
    fun getData(key: String) : String? {
        if (key == null) {
            throw IllegalArgumentException("RedisUtil getData: " + key + "is null")
        }
        val valueOperations = redisTemplate.opsForValue()
        return valueOperations.get(key)
    }

    fun setData(key: String, value: String) {
        val valueOperations = redisTemplate.opsForValue()
        valueOperations.set(key, value)
    }

    fun setDataExpire(key: String, value: String, duration: Long) {
        val valueOperations = redisTemplate.opsForValue()
        val expireDuration = Duration.ofSeconds(duration)
        valueOperations.set(key, value, expireDuration)
    }

    fun deleteData(key: String) {
        redisTemplate.delete(key)
    }
}