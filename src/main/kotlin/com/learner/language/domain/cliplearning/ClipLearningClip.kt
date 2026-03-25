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
    name = "clip_learning_clip",
    indexes = [
        Index(name = "idx_clip_learning_clip_source_video_id", columnList = "source_video_id"),
        Index(name = "idx_clip_learning_clip_category", columnList = "category"),
        Index(name = "uk_clip_learning_clip_source_video_range", columnList = "source_video_id, clip_start_ms, clip_end_ms", unique = true)
    ]
)
class ClipLearningClip(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_video_id", nullable = false)
    var sourceVideo: ClipSourceVideo,

    @Column(name = "title", nullable = false, length = 255)
    var title: String,

    @Column(name = "category", nullable = false, length = 100)
    var category: String,

    @Column(name = "clip_start_ms", nullable = false)
    var clipStartMs: Long,

    @Column(name = "clip_end_ms", nullable = false)
    var clipEndMs: Long,

    @Column(name = "clip_duration_ms", nullable = false)
    var clipDurationMs: Long,
) : BaseEntity()
