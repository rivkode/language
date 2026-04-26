CREATE TABLE shorts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    youtube_video_id VARCHAR(11) NOT NULL,
    source_url VARCHAR(255) NOT NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_shorts_youtube_video_id (youtube_video_id),
    KEY idx_shorts_active_created_at (is_active, created_at, id),
    CONSTRAINT ck_shorts_youtube_video_id_len CHECK (CHAR_LENGTH(youtube_video_id) = 11)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_saved_short (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    short_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_saved_short_user FOREIGN KEY (user_id) REFERENCES `user`(id),
    CONSTRAINT fk_user_saved_short_short FOREIGN KEY (short_id) REFERENCES shorts(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_saved_short_user_short (user_id, short_id),
    KEY idx_user_saved_short_user_id_created_at (user_id, created_at),
    KEY idx_user_saved_short_short_id (short_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO shorts (youtube_video_id, source_url)
VALUES
    ('fxqR_mh03YI', 'https://www.youtube.com/shorts/fxqR_mh03YI'),
    ('fo49QHjmk74', 'https://www.youtube.com/shorts/fo49QHjmk74'),
    ('mDCduZ3JwZQ', 'https://www.youtube.com/shorts/mDCduZ3JwZQ'),
    ('ql7Xr1YVgcI', 'https://www.youtube.com/shorts/ql7Xr1YVgcI'),
    ('ezKHBhWiE84', 'https://www.youtube.com/shorts/ezKHBhWiE84'),
    ('w0jePoXgL0k', 'https://www.youtube.com/shorts/w0jePoXgL0k');

INSERT INTO clip_source_video (id, youtube_video_id, source_url, source_title, channel_name, thumbnail_url)
VALUES
    (802, 'TmkcVc60x8Y', 'https://www.youtube.com/watch?v=TmkcVc60x8Y', 'Clip 2 source',
     'Clip Source Channel', 'https://img.youtube.com/vi/TmkcVc60x8Y/hqdefault.jpg'),
    (803, 'QrGbJz5H1PU', 'https://www.youtube.com/watch?v=QrGbJz5H1PU', 'Clip 3 source',
     'Clip Source Channel', 'https://img.youtube.com/vi/QrGbJz5H1PU/hqdefault.jpg'),
    (804, 'opqq01bu764', 'https://www.youtube.com/watch?v=opqq01bu764', 'Clip 4 source',
     'Clip Source Channel', 'https://img.youtube.com/vi/opqq01bu764/hqdefault.jpg'),
    (805, 'd8v0hgMJZDU', 'https://www.youtube.com/watch?v=d8v0hgMJZDU', 'Clip 5 source',
     'Clip Source Channel', 'https://img.youtube.com/vi/d8v0hgMJZDU/hqdefault.jpg');

INSERT INTO clip_learning_clip (id, source_video_id, title, category, clip_start_ms, clip_end_ms, clip_duration_ms)
VALUES
    (2002, 802, 'Clip 2', 'general', 0, 0, 0),
    (2003, 803, 'Clip 3', 'general', 0, 0, 0),
    (2004, 804, 'Clip 4', 'general', 0, 0, 0),
    (2005, 805, 'Clip 5', 'general', 0, 0, 0);
