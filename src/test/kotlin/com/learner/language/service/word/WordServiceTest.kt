//package com.learner.language.service.word
//
//import com.learner.language.domain.user.UserReader
//import com.learner.language.domain.word.Part
//import com.learner.language.domain.word.WordReader
//import com.learner.language.domain.word.WordServiceImpl
//import com.learner.language.domain.word.WordWriter
//import com.learner.language.domain.word.review.WordReviewCountReaderImpl
//import com.learner.language.domain.word.review.WordReviewCountWriterImpl
//import com.learner.language.infrastructure.word.wordusermatch.WordUserMatchRepository
//import com.learner.language.testutils.UnitTest
//import com.learner.language.testutils.fixture.WordFixture
//import io.kotest.matchers.shouldBe
//import io.mockk.every
//import io.mockk.mockk
//
//class WordServiceTest : UnitTest() {
//    private val wordReader: WordReader = mockk()
//    private val wordWriter: WordWriter = mockk()
//    private val userReader: UserReader = mockk()
//    private val wordReviewCountReaderImpl: WordReviewCountReaderImpl = mockk()
//    private val wordReviewCountWriterImpl: WordReviewCountWriterImpl = mockk()
//    private val wordUserMatchRepository: WordUserMatchRepository = mockk()
//
//    private val wordServiceImpl = WordServiceImpl(
//        wordReader = wordReader,
//        wordWriter = wordWriter,
//        userReader = userReader,
//        wordReviewCountReaderImpl = wordReviewCountReaderImpl,
//        wordReviewCountWriterImpl = wordReviewCountWriterImpl,
//        wordUserMatchRepository = wordUserMatchRepository
//    )
//
//    init {
//        "part 별 단어를 조회한다" {
//            val part = Part.NOUN
//
//            val wordInfo = WordFixture.createWord(
//                part = part
//
//            )
//
//            val wordInfos = listOf(wordInfo,wordInfo,wordInfo)
//            val wordIds = listOf(1L)
//            val userId = 1L
//            val lastId = 2L
//            val pageSize = 3
//
//            every { wordUserMatchRepository.findWordIdsByUserId(any()) } returns wordIds
//            every { wordReader.getChoiceWord(part, eq(listOf(userId)), lastId, pageSize) } returns wordInfos
//
//            val actual = wordServiceImpl.getChoiceWord(part, userId, lastId)
//
//            actual.forEachIndexed { index, actualData ->
//                actualData.part shouldBe part
//            }
//
//        }
//    }
//
//}
