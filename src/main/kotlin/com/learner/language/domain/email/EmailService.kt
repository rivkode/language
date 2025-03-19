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
        val title = "[Lingo] 인증 이메일입니다."
        val htmlBody = """
            <div style='font-family: Arial, sans-serif;'>
                <p>안녕하세요, $email 고객님</p>
                <br><br>
                <p>[Lingo] 을 방문해주셔서 감사합니다.</p>
                <br>
                <p>아래 발급된 이메일 인증번호를 복사하거나 직접 입력하여 인증을 완료해주세요.</p>
                <br>
                <p>개인정보 보호를 위해 인증번호는 5분 간 유효합니다.</p>
                <br><br>
                <p style='font-size: 20px; font-weight: bold; color: #0077ff;'>$authNumber</p>
                <br><br>
                <p>인증번호를 입력해주시면 회원가입이 완료됩니다.</p>
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
