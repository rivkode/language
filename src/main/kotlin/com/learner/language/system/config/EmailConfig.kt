package com.learner.language.system.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import io.github.cdimascio.dotenv.dotenv

import java.util.*

@Configuration
class EmailConfig {

    private final val dotenv = dotenv()
    val emailUsername: String = dotenv["EMAIL_USERNAME"]
    val emailPassword: String = dotenv["EMAIL_PASSWORD"]


    @Bean
    fun javaMailSender(): JavaMailSender {
        return JavaMailSenderImpl().apply {
            host = "smtp.gmail.com"
            port = 587
            username = emailUsername
            password = emailPassword

            javaMailProperties = Properties().apply {
                put("mail.transport.protocol", "smtp")
                put("mail.smtp.auth", "true")
                put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                put("mail.smtp.starttls.enable", "true")
                put("mail.debug", "true")
                put("mail.smtp.ssl.trust", "smtp.google.com")
                put("mail.smtp.ssl.protocols", "TLSv1.2")
            }
        }
    }
}