package com.learner.language.domain.diary

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "diary_tag")
class DiaryTag(
    @Column(name = "diary_id", nullable = false)
    var diaryId: Long,

    @Column(name = "tag", nullable = false, length = Diary.TAG_MAX_LENGTH)
    var tag: String,
) : BaseEntity()
