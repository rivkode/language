package com.learner.language.integration.word

import com.learner.language.domain.word.Part
import com.learner.language.domain.word.WordReader
import com.learner.language.interfaces.word.WordDto
import com.learner.language.testutils.IntegrationTest
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

class WordIntegrationTest : IntegrationTest() {
    @Autowired
    private lateinit var wordReader: WordReader

    init {
        // 사용자가 선택했는지 체크 하지 않음
        given("part별 단어 조회") {

            wordPersistenceUtils.bulkSaveNewWord()

            `when`("NOUN 파트 단어 조회") {
                val email = "test@gmail.com"
                val password = "password"
                userPersistenceUtils.saveNewUser(email, password)

                val uri = "/api/v1/words/choice"
                val jwtToken = getJwtToken(email, password)
                    .replace("Bearer ", "")
                    .trim()
                println("JWT Token {$jwtToken}")
                val mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.get(uri).param("part", Part.NOUN.name)
                        .with(authorizedRequest(jwtToken = jwtToken))
                )
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn()
                
                then("단어 정보를 반환") {
                    mvcResult.response.status shouldBe HttpStatus.OK.value()

                    val actual = objectMapper.readValue(
                        mvcResult.response.contentAsString,
                        WordDto.RetrieveWordInfoListResponse::class.java
                    )

                    val lastId = 2L
                    val pageSize = 3

                    val dbWordMetadata = wordReader.getChoiceWord(Part.NOUN, listOf(), lastId, pageSize)

                    actual.wordInfoList.zip(dbWordMetadata).forEach { (actualData, expectedResult) ->
                        actualData.part shouldBe expectedResult.part
                    }
                }
            }
        }
    }

    fun getJwtToken(email: String, password: String): String {
        val loginResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "email": "${email}",
                    "password": "${password}"
                }
                """.trimIndent()
                )
        )
            .andReturn()

        return loginResponse.response.getHeader("Authorization") ?: throw RuntimeException("JWT 토큰을 가져오지 못했습니다.")
    }

    fun authorizedRequest(jwtToken: String): RequestPostProcessor {
        return RequestPostProcessor { request ->
            request.addHeader("Authorization", "Bearer $jwtToken")
            request
        }
    }

}
