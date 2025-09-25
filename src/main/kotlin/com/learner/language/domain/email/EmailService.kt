package com.learner.language.domain.email

import com.learner.language.utils.RedisUtil
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.MimeMessageHelper


@Service
class MailService(
    private val javaMailSender: JavaMailSender,
    private val redisUtil: RedisUtil
) {

    @Async
    fun sendValidateEmail(email: String, authNumber: String) {
        val from = "jonghuncu@gmail.com"
        val to = email
        val title = "[Jamo] Verification Email"
        val htmlBody = """
            <div style='font-family: Arial, sans-serif;'>
                <p>Hello $email,</p>
                <br><br>
                <p>Welcome to Jamo!</p>
                <br>
                <p>To verify your email address and complete your registration, please use the following one-time verification code:</p>
                <br>
                <p>This code will expire in 5 minutes for your security.</p>
                <br><br>
                <p style='font-size: 20px; font-weight: bold; color: #0077ff;'>$authNumber</p>
                <br><br>
                <p>Enter this code on the Lingo website to finalize your account setup. If you did not request this verification, please disregard this email.</p>
            </div>
        """.trimIndent()

        redisUtil.setDataExpire(authNumber, to, 60 * 5L)
        sendMail(from, to, title, htmlBody)
    }

    private fun sendMail(from: String, to: String, title: String, content: String) {
        val message: MimeMessage = javaMailSender.createMimeMessage()
        try {
            MimeMessageHelper(message, true, "utf-8").apply {
                setFrom(from)
                setTo(to)
                setSubject(title)
                setText(content, true)
            }
            javaMailSender.send(message)
        } catch (e: MessagingException) {
            log.error("이메일 전송 실패: ${e.message}", e)
        }
    }

    fun checkAuthNumber(email: String, authNumber: String): Boolean {
        return redisUtil.getData(authNumber)?.let { it == email } ?: false
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(MailService::class.java)
    }
}
