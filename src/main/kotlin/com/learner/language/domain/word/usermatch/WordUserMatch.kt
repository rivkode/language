package com.learner.language.domain.word.usermatch

import com.learner.language.common.BaseEntity
import com.learner.language.domain.user.User
import com.learner.language.domain.word.Word
import jakarta.persistence.*

@Entity
@Table(name = "word_user_match")
class WordUserMatch(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    val word: Word,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
): BaseEntity()