package com.learner.language.testutils.fixture

import com.learner.language.domain.word.CefrLevel
import com.learner.language.domain.word.Part
import com.learner.language.domain.word.Word

object WordFixture {
    fun createWord(
        label: String = "yello",
        description: String = "similar color like sand",
        part: Part = Part.NOUN,
        level: CefrLevel = CefrLevel.A1
    ): Word {
        return Word(
            label = label,
            enMeaning = description,
            part = part,
            level = level,
            krExample = "노란",
            enExample = "this is yello",
            ipa = "no-ran"
        )
    }
}
