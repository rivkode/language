package com.learner.language.domain.word

import com.learner.language.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "word_list")
class WordList(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "word_list_id", nullable = false)
    var wordListId: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    val word: Word,


) : BaseEntity() {
}