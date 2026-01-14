package com.learner.language.domain.sentence.wordmatch

import com.learner.language.common.BaseEntity
import com.learner.language.domain.sentence.Sentence
import com.learner.language.domain.word.Word
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "sentence_word_match")
class SentenceWordMatch(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sentence_id")
    val sentence: Sentence,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    val word: Word,

): BaseEntity()
