package com.learner.language.domain.cliplearning

import com.learner.language.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "clip_source_video",
    indexes = [
        Index(name = "idx_clip_source_video_youtube_video_id", columnList = "youtube_video_id", unique = true)
    ]
)
class ClipSourceVideo(
    @Column(name = "youtube_video_id", nullable = false, length = 100)
    var youtubeVideoId: String,

    @Column(name = "source_url", nullable = false, length = 1000)
    var sourceUrl: String,

    @Column(name = "source_title", nullable = false, length = 255)
    var sourceTitle: String,

    @Column(name = "channel_name", nullable = false, length = 255)
    var channelName: String,

    @Column(name = "thumbnail_url", length = 1000)
    var thumbnailUrl: String?,
) : BaseEntity()
