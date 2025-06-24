package com.learner.language.domain.word

import com.learner.language.domain.event.Event
import com.learner.language.domain.event.WordReviewEvent
import com.learner.language.domain.user.UserReader
import com.learner.language.domain.word.review.WordReviewCount
import com.learner.language.domain.word.review.WordReviewCountReaderImpl
import com.learner.language.domain.word.review.WordReviewCountWriterImpl
import com.learner.language.infrastructure.word.wordusermatch.WordUserMatchRepository
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class WordServiceImpl(
    private val wordReader: WordReader,
    private val wordWriter: WordWriter,
    private val userReader: UserReader,
    private val wordReviewCountReaderImpl: WordReviewCountReaderImpl,
    private val wordReviewCountWriterImpl: WordReviewCountWriterImpl,
    private val wordUserMatchRepository: WordUserMatchRepository
) : WordService {

    override fun registerWord(wordRegisterCommand: WordCommand.RegisterWord): WordInfo {
        val word = wordWriter.save(wordRegisterCommand.toEntity())
        val wordInfo = WordInfo(word)

        return wordInfo
    }

    override fun getChoiceWord(part: Part, userId: Long, lastWordId: Long?): List<WordInfo> {
        val wordIds = wordUserMatchRepository.findWordIdsByUserId(userId=userId)
        val pageSize = 3

        val words = wordReader.getChoiceWord(part, wordIds, lastWordId, pageSize)
        val wordInfos = WordInfo.from(words)

        return wordInfos
    }

    override fun saveChoiceWord(command: WordCommand.RegisterChoiceWord): WordInfo {
        val user = userReader.getUserById(command.userId)
        val word = wordReader.getWordById(command.wordId)

        val wordUserMatch = command.toEntity(word, user)
        wordUserMatchRepository.save(wordUserMatch)

        val wordInfo = WordInfo(word)

        return wordInfo
    }

    override fun getReviewWords(userId: Long, wordListId: Int, count: Int): ReviewWords {
        val noCountWord = wordReader.getNoCountReviewWords(userId = userId, wordListId = wordListId)
        val countOneWord = wordReader.getCountReviewWords(userId = userId, wordListId = wordListId, count = 1)
        val countTwoWord = wordReader.getCountReviewWords(userId = userId, wordListId = wordListId, count = 2)

        val noCountWordInfo = buildWordInfos(words = noCountWord, count = count)
        val countOneWordInfo = buildWordInfos(words = countOneWord, count = count)
        val countTwoWordInfo = buildWordInfos(words = countTwoWord, count = count)

        val reviewWords = ReviewWords(noCountWordInfo = noCountWordInfo, countOneWordInfo = countOneWordInfo, countTwoWordInfo = countTwoWordInfo)

        return reviewWords
    }

    override fun buildWordInfos(words: List<Word>, count: Int): List<WordInfo> {
        // check wordInfo morethan 20
        if (count <= 0) {
            return emptyList()
        }

        if (count >= words.size) {
            val randomWords = words.shuffled()
            val countWordInfo = randomWords.map(transform = { WordInfo(it) })

            return countWordInfo
        }

        val random = Random.Default
        val indices = words.indices.shuffled(random).take(count).sorted()
        val randomWords =  indices.map { words[it] }
        val countWordInfo = randomWords.map(transform = { WordInfo(it) })

        return countWordInfo
    }

    override fun eventProcess(event: Event) {
        if (event is WordReviewEvent) {

            if (event.known == false) {
                return
            } else {
                // 경험치

                val wordReviewCount = wordReviewCountReaderImpl.getWordReviewCount(
                    userId = event.userId,
                    wordId = event.wordId
                )

                if (wordReviewCount == null) {

                    val word = wordReader.getWordById(event.wordId)
                    val user = userReader.getUserById(event.userId)

                    val newWordReviewCount = WordReviewCount(
                        word = word,
                        user = user,
                        count = 0
                    )
                    wordReviewCountWriterImpl.save(newWordReviewCount)
                } else {
                    wordReviewCount.count += 1
                    wordReviewCountWriterImpl.save(wordReviewCount)
                }
            }
        }


    }


    override fun getMyWordList(userId: Long) : List<WordInfo> {
        val wordIds = wordUserMatchRepository.findWordIdsByUserId(userId)
        val myWordList = wordReader.getWordListByIds(wordIds)

        return myWordList.map(transform = { WordInfo(it) })
    }
}
