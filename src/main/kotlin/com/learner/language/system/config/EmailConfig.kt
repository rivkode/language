package com.learner.language.system.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

import java.util.*

@Configuration
class EmailConfig {

    @Value("\${app.email.username}")
    val emailUsername: String = ""

    @Value("\${app.email.password}")
    val emailPassword: String = ""

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