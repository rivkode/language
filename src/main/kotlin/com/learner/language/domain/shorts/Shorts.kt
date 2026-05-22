package com.learner.language.domain.shorts

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "shorts",
    indexes = [
        Index(name = "uk_shorts_youtube_video_id", columnList = "youtube_video_id", unique = true),
        Index(name = "idx_shorts_active_created_at", columnList = "is_active, created_at, id"),
    ]
)
class Shorts(
    @Column(name = "youtube_video_id", nullable = false, length = 11)
    var youtubeVideoId: String,

    @Column(name = "source_url", nullable = false, length = 255)
    var sourceUrl: String,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,
) : BaseEntity()
