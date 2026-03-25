package com.learner.language.domain.cliplearning

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "clip_learning_sentence",
    indexes = [
        Index(name = "uk_clip_learning_sentence_clip_id", columnList = "clip_id", unique = true)
    ]
)
class ClipLearningSentence(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip_id", nullable = false, unique = true)
    var clip: ClipLearningClip,

    @Column(name = "primary_sentence", nullable = false, length = 2000)
    var primarySentence: String,

    @Column(name = "translation", length = 2000)
    var translation: String?,

    @Lob
    @Column(name = "explanation_summary", columnDefinition = "TEXT")
    var explanationSummary: String?,

    @Lob
    @Column(name = "usage_tip", columnDefinition = "TEXT")
    var usageTip: String?,
) : BaseEntity()
