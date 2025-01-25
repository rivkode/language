package com.learner.language.infrastructure.word

import com.learner.language.domain.word.Word
import com.learner.language.domain.word.WordReader
import org.springframework.stereotype.Component

@Component
class WordReaderImpl(
    private val wordRepository: WordRepository
) : WordReader {
    override fun getWordListByIds(ids: List<Long>) : List<Word> {
        return wordRepository.findAllById(ids).toList()
    }

    override fun getWordById(id: Long) : Word{
        return wordRepository.findById(id).get()
    }
}
