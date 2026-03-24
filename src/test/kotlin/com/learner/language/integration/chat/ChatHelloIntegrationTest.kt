package com.learner.language.integration.chat

import com.learner.language.interfaces.chat.ChatDto
import com.learner.language.testutils.IntegrationTest
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.RequestPostProcessor

class ChatHelloIntegrationTest : IntegrationTest() {

    init {
        given("인증된 사용자가 chat hello api를 호출하면") {
            val email = "chat-hello@test.com"
            val password = "password123"
            val username = "chat-user"
            userPersistenceUtils.saveNewUser(email = email, password = password, username = username)

            `when`("GET /api/v1/chat/hello 요청을 보내면") {
                val jwtToken = getJwtToken(email, password)
                val mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/chat/hello")
                        .with(authorizedRequest(jwtToken))
                ).andReturn()

                then("hello 응답을 반환한다") {
                    mvcResult.response.status shouldBe HttpStatus.OK.value()

                    val actual = objectMapper.readValue(
                        mvcResult.response.contentAsString,
                        ChatDto.HelloResponse::class.java
                    )

                    actual.message shouldBe "hello"
                }
            }
        }
    }

    private fun getJwtToken(email: String, password: String): String {
        val loginResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "email": "$email",
                        "password": "$password"
                    }
                    """.trimIndent()
                )
        ).andReturn()

        loginResponse.response.status shouldBe HttpStatus.OK.value()

        return loginResponse.response.getHeader(HttpHeaders.AUTHORIZATION)
            ?: throw IllegalStateException("Authorization header is missing")
    }

    private fun authorizedRequest(jwtToken: String): RequestPostProcessor {
        return RequestPostProcessor { request ->
            request.addHeader(HttpHeaders.AUTHORIZATION, jwtToken)
            request
        }
    }
}
