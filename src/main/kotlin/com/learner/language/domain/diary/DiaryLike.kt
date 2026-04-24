package com.learner.language.domain.diary

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "diary_like")
class DiaryLike(
    @Column(name = "diary_id", nullable = false)
    var diaryId: Long,

    @Column(name = "user_id", nullable = false)
    var userId: Long,
) : BaseEntity()
