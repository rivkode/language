package com.learner.language.domain.cliplearning

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "clip_learning_vocabulary",
    indexes = [
        Index(name = "idx_clip_learning_vocabulary_clip_id", columnList = "clip_id"),
        Index(name = "uk_clip_learning_vocabulary_clip_order", columnList = "clip_id, display_order", unique = true)
    ]
)
class ClipLearningVocabulary(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clip_id", nullable = false)
    var clip: ClipLearningClip,

    @Column(name = "word", nullable = false, length = 255)
    var word: String,

    @Column(name = "meaning", nullable = false, length = 1000)
    var meaning: String,

    @Column(name = "display_order", nullable = false)
    var displayOrder: Int,
) : BaseEntity()
