package com.learner.language.domain.word

import com.learner.language.domain.word.usermatch.WordUserMatchRepository
import org.springframework.stereotype.Component

@Component
class WordServiceImpl(
    private val wordReader: WordReader,
    private val wordWriter: WordWriter,
    private val wordUserMatchRepository: WordUserMatchRepository
) : WordService {

    override fun registerWord(wordRegisterCommand: WordCommand.RegisterWord): WordInfo {
        val word = wordWriter.save(wordRegisterCommand.toEntity())
        val wordInfo = WordInfo(word)

        return wordInfo
    }

    override fun getMyWordList(wordRetrieveCommand: WordCommand.RetrieveMyWord) : List<WordInfo> {
        val wordIds = wordUserMatchRepository.findWordIdsByUserId(wordRetrieveCommand.userId)
        val myWordList = wordReader.getWordListByIds(wordIds)

        return myWordList.map(transform = { WordInfo(it) })
    }
}
