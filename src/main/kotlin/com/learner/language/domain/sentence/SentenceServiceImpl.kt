package com.learner.language.domain.sentence

import com.learner.language.domain.sentence.wordmatch.SentenceWordMatch
import com.learner.language.domain.user.UserReader
import com.learner.language.domain.word.WordInfo
import com.learner.language.domain.word.WordReader
import com.learner.language.infrastructure.sentence.sentencewordmatch.SentenceWordMatchRepository
import org.springframework.stereotype.Component

@Component
class SentenceServiceImpl(
    private val sentenceReader: SentenceReader,
    private val sentenceWriter: SentenceWriter,
    private val userReader: UserReader,
    private val wordReader: WordReader,
    private val sentenceWordMatchRepository: SentenceWordMatchRepository,

    ) : SentenceService {
    override fun saveSentence(command: SentenceCommand.Register, userId: Long): SentenceInfo {
        val user = userReader.getUserById(userId)
        val savedSentence = sentenceWriter.save(command.toEntity(user))
        val sentenceWordList: MutableList<SentenceWordMatch> = mutableListOf()

        for (wordId in command.wordIds) {
            val word = wordReader.getWordById(wordId)
            sentenceWordList.add(SentenceWordMatch(savedSentence, word))
        }

        sentenceWordMatchRepository.saveAll(sentenceWordList)

        val sentenceInfo = SentenceInfo(savedSentence)

        return sentenceInfo
    }

    override fun getMySentenceList(userId: Long): List<SentenceInfo> {
        val sentenceList = sentenceReader.getSentenceListByUserId(userId)
        val sentenceInfoList: MutableList<SentenceInfo> = mutableListOf()

        for (sentence in sentenceList) {
            val words = wordReader.getWordListBySentenceId(sentence.id)
            val wordListInfo = WordInfo.from(words)
            val sentenceInfo = SentenceInfo(sentence.id, userId, sentence.userSentence, wordListInfo)
            sentenceInfoList.add(sentenceInfo)
        }

        return sentenceInfoList
    }

    override fun getMySentence(userId: Long, sentenceId: Long): SentenceInfo {
        val sentence = sentenceReader.getUserSentence(userId, sentenceId)

        val words = wordReader.getWordListBySentenceId(sentence.id)
        val wordListInfo = WordInfo.from(words)
        val sentenceInfo = SentenceInfo(sentence.id, userId, sentence.userSentence, wordListInfo)

        return sentenceInfo
    }

}
