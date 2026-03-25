package com.learner.language.integration.profile

import com.learner.language.interfaces.profile.ProfileDto
import com.learner.language.interfaces.profile.ProfileSavedClipDto
import com.learner.language.testutils.IntegrationTest
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.RequestPostProcessor

class ProfileIntegrationTest : IntegrationTest() {

    init {
        given("인증된 사용자가 다른 사용자의 프로필을 조회하면") {
            userPersistenceUtils.saveNewUser(
                email = "viewer@test.com",
                password = "password123",
                username = "viewer"
            )
            val profileUser = userPersistenceUtils.saveNewUser(
                email = "profile@test.com",
                password = "password123",
                username = "profile-user"
            )
            profilePersistenceUtils.saveUserProfile(
                user = profileUser,
                displayName = "Profile User",
                bio = "saved clip collector",
                profileImageUrl = "https://cdn.example.com/profile-user.jpg"
            )

            `when`("GET /api/v1/profiles/{userId} 요청을 보내면") {
                val jwtToken = getJwtToken("viewer@test.com", "password123")
                val mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/profiles/${profileUser.id}")
                        .with(authorizedRequest(jwtToken))
                ).andReturn()

                then("프로필 헤더를 반환한다") {
                    mvcResult.response.status shouldBe HttpStatus.OK.value()
                    val response = objectMapper.readValue(
                        mvcResult.response.contentAsString,
                        ProfileDto.ProfileResponse::class.java
                    )

                    response.userId shouldBe profileUser.id
                    response.username shouldBe "profile-user"
                    response.displayName shouldBe "Profile User"
                    response.bio shouldBe "saved clip collector"
                    response.isMe shouldBe false
                }
            }
        }

        given("사용자가 저장한 clip 피드를 조회하면") {
            val owner = userPersistenceUtils.saveNewUser(
                email = "saved-clips@test.com",
                password = "password123",
                username = "saved-owner"
            )
            val sourceVideo = clipLearningPersistenceUtils.saveSourceVideo(
                youtubeVideoId = "FB_Lh9vq4Wc",
                sourceUrl = "https://youtube.com/watch?v=FB_Lh9vq4Wc&pp=ygUT7JWE7J2064-MIOyduO2EsOu3sA%3D%3D",
                sourceTitle = "아이들 인터뷰 클립 1",
                channelName = "Clip Learning Samples",
                thumbnailUrl = "https://img.youtube.com/vi/FB_Lh9vq4Wc/hqdefault.jpg"
            )
            val clip1 = clipLearningPersistenceUtils.saveClip(
                sourceVideo = sourceVideo,
                title = "감정 표현 따라 말하기",
                category = "daily-conversation",
                clipStartMs = 12_000L,
                clipEndMs = 21_500L,
                primarySentence = "진짜 너무 떨렸어요."
            )
            val clip2 = clipLearningPersistenceUtils.saveClip(
                sourceVideo = sourceVideo,
                title = "리액션 문장 익히기",
                category = "daily-conversation",
                clipStartMs = 24_000L,
                clipEndMs = 33_200L,
                primarySentence = "생각보다 훨씬 재미있었어요."
            )
            clipLearningPersistenceUtils.saveSavedClip(owner, clip1)
            clipLearningPersistenceUtils.saveSavedClip(owner, clip2)

            `when`("GET /api/v1/profiles/{userId}/saved-clips 요청을 보내면") {
                val jwtToken = getJwtToken("saved-clips@test.com", "password123")
                val mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/profiles/${owner.id}/saved-clips")
                        .param("size", "12")
                        .with(authorizedRequest(jwtToken))
                ).andReturn()

                then("저장한 clip 피드 목록을 반환한다") {
                    mvcResult.response.status shouldBe HttpStatus.OK.value()
                    val response = objectMapper.readValue(
                        mvcResult.response.contentAsString,
                        ProfileSavedClipDto.FeedResponse::class.java
                    )

                    response.items.size shouldBe 2
                    response.items.first().clip.youtubeVideoId shouldBe "FB_Lh9vq4Wc"
                    response.items.first().clip.category shouldBe "daily-conversation"
                    response.paging.hasNext shouldBe false
                }
            }
        }

        given("인증된 사용자가 내 프로필을 수정하면") {
            userPersistenceUtils.saveNewUser(
                email = "me@test.com",
                password = "password123",
                username = "my-username"
            )

            `when`("PATCH /api/v1/profiles/me 요청을 보내면") {
                val jwtToken = getJwtToken("me@test.com", "password123")
                val mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.patch("/api/v1/profiles/me")
                        .with(authorizedRequest(jwtToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            """
                            {
                                "displayName": "My Display Name",
                                "bio": "I love saved clips",
                                "profileImageUrl": "https://cdn.example.com/me.jpg"
                            }
                            """.trimIndent()
                        )
                ).andReturn()

                then("프로필 수정 결과를 반환한다") {
                    mvcResult.response.status shouldBe HttpStatus.OK.value()
                    val response = objectMapper.readValue(
                        mvcResult.response.contentAsString,
                        ProfileDto.ProfileResponse::class.java
                    )

                    response.displayName shouldBe "My Display Name"
                    response.bio shouldBe "I love saved clips"
                    response.profileImageUrl shouldBe "https://cdn.example.com/me.jpg"
                    response.isMe shouldBe true
                }
            }
        }

        given("인증된 사용자가 내 프로필을 빠르게 조회하면") {
            val me = userPersistenceUtils.saveNewUser(
                email = "me-quick@test.com",
                password = "password123",
                username = "quick-user"
            )
            profilePersistenceUtils.saveUserProfile(
                user = me,
                displayName = "Quick User",
                bio = "quick profile",
                profileImageUrl = "https://cdn.example.com/quick.jpg"
            )

            `when`("GET /api/v1/profiles/me 요청을 보내면") {
                val jwtToken = getJwtToken("me-quick@test.com", "password123")
                val mvcResult = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/profiles/me")
                        .with(authorizedRequest(jwtToken))
                ).andReturn()

                then("내 프로필 정보를 반환한다") {
                    mvcResult.response.status shouldBe HttpStatus.OK.value()
                    val response = objectMapper.readValue(
                        mvcResult.response.contentAsString,
                        ProfileDto.ProfileResponse::class.java
                    )

                    response.userId shouldBe me.id
                    response.displayName shouldBe "Quick User"
                    response.isMe shouldBe true
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
