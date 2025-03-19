package com.learner.language.domain.word

import com.learner.language.domain.user.UserReader
import com.learner.language.infrastructure.word.wordusermatch.WordUserMatchRepository
import org.springframework.stereotype.Component

@Component
class WordServiceImpl(
    private val wordReader: WordReader,
    private val wordWriter: WordWriter,
    private val userReader: UserReader,
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

    override fun getMyWordList(userId: Long) : List<WordInfo> {
        val wordIds = wordUserMatchRepository.findWordIdsByUserId(userId)
        val myWordList = wordReader.getWordListByIds(wordIds)

        return myWordList.map(transform = { WordInfo(it) })
    }
}
