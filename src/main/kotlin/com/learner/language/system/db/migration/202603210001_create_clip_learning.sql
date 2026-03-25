CREATE TABLE clip_source_video (
    id BIGINT NOT NULL AUTO_INCREMENT,
    youtube_video_id VARCHAR(100) NOT NULL,
    source_url VARCHAR(1000) NOT NULL,
    source_title VARCHAR(255) NOT NULL,
    channel_name VARCHAR(255) NOT NULL,
    thumbnail_url VARCHAR(1000) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_clip_source_video_youtube_video_id (youtube_video_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE clip_learning_clip (
    id BIGINT NOT NULL AUTO_INCREMENT,
    source_video_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    clip_start_ms BIGINT NOT NULL,
    clip_end_ms BIGINT NOT NULL,
    clip_duration_ms BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_clip_learning_clip_source_video FOREIGN KEY (source_video_id) REFERENCES clip_source_video(id),
    UNIQUE KEY uk_clip_learning_clip_source_video_range (source_video_id, clip_start_ms, clip_end_ms),
    KEY idx_clip_learning_clip_source_video_id (source_video_id),
    KEY idx_clip_learning_clip_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE clip_learning_sentence (
    id BIGINT NOT NULL AUTO_INCREMENT,
    clip_id BIGINT NOT NULL,
    primary_sentence VARCHAR(2000) NOT NULL,
    translation VARCHAR(2000) NULL,
    explanation_summary TEXT NULL,
    usage_tip TEXT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_clip_learning_sentence_clip FOREIGN KEY (clip_id) REFERENCES clip_learning_clip(id),
    UNIQUE KEY uk_clip_learning_sentence_clip_id (clip_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE clip_learning_vocabulary (
    id BIGINT NOT NULL AUTO_INCREMENT,
    clip_id BIGINT NOT NULL,
    word VARCHAR(255) NOT NULL,
    meaning VARCHAR(1000) NOT NULL,
    display_order INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_clip_learning_vocabulary_clip FOREIGN KEY (clip_id) REFERENCES clip_learning_clip(id),
    KEY idx_clip_learning_vocabulary_clip_id (clip_id),
    UNIQUE KEY uk_clip_learning_vocabulary_clip_order (clip_id, display_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_saved_clip (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    clip_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_saved_clip_user FOREIGN KEY (user_id) REFERENCES `user`(id),
    CONSTRAINT fk_user_saved_clip_clip FOREIGN KEY (clip_id) REFERENCES clip_learning_clip(id),
    UNIQUE KEY uk_user_saved_clip_user_clip (user_id, clip_id),
    KEY idx_user_saved_clip_user_id_created_at (user_id, created_at),
    KEY idx_user_saved_clip_clip_id (clip_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_clip_learning_progress (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    clip_id BIGINT NOT NULL,
    last_viewed_position_ms BIGINT NOT NULL DEFAULT 0,
    repeat_enabled TINYINT(1) NOT NULL DEFAULT 0,
    translation_visible TINYINT(1) NOT NULL DEFAULT 0,
    completed TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_clip_learning_progress_user FOREIGN KEY (user_id) REFERENCES `user`(id),
    CONSTRAINT fk_user_clip_learning_progress_clip FOREIGN KEY (clip_id) REFERENCES clip_learning_clip(id),
    UNIQUE KEY uk_user_clip_progress_user_clip (user_id, clip_id),
    KEY idx_user_clip_progress_user_id_updated_at (user_id, updated_at),
    KEY idx_user_clip_progress_clip_id (clip_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO clip_source_video (id, youtube_video_id, source_url, source_title, channel_name, thumbnail_url)
VALUES
    (501, 'a1b2c3d4e5', 'https://www.youtube.com/watch?v=a1b2c3d4e5', 'Cafe Expressions in Korean', 'Korean Daily Clips', 'https://img.youtube.com/vi/a1b2c3d4e5/hqdefault.jpg'),
    (601, 'f6g7h8i9j0', 'https://www.youtube.com/watch?v=f6g7h8i9j0', 'Office Korean Basics', 'Office Korean Lab', 'https://img.youtube.com/vi/f6g7h8i9j0/hqdefault.jpg'),
    (701, 'p6q7r8s9t0', 'https://www.youtube.com/watch?v=p6q7r8s9t0', 'Travel Korean Essentials', 'Travel Korean Guide', 'https://img.youtube.com/vi/p6q7r8s9t0/hqdefault.jpg');

INSERT INTO clip_learning_clip (id, source_video_id, title, category, clip_start_ms, clip_end_ms, clip_duration_ms)
VALUES
    (1001, 501, 'Ordering Coffee Naturally', 'daily-conversation', 12000, 21500, 9500),
    (1002, 501, 'Answering at the Cafe Counter', 'daily-conversation', 24000, 33000, 9000),
    (1003, 601, 'Introducing Yourself at Work', 'business-korean', 8500, 17000, 8500),
    (1004, 601, 'Polite Follow-up in Meetings', 'business-korean', 19000, 29000, 10000),
    (1005, 701, 'Asking for Directions', 'travel', 14000, 23500, 9500),
    (1006, 701, 'Buying a Ticket', 'travel', 26000, 35000, 9000);

INSERT INTO clip_learning_sentence (clip_id, primary_sentence, translation, explanation_summary, usage_tip)
VALUES
    (1001, '아이스 아메리카노 한 잔 주세요.', 'I''d like one iced Americano, please.', 'Useful Korean expression for daily conversation situations.', 'Practice it as a short speaking pattern and repeat with the clip loop.'),
    (1002, '매장에서 드시고 가세요?', 'Will you have it here?', 'Useful Korean expression for daily conversation situations.', 'Listen for the polite ending and practice shadowing it.'),
    (1003, '이번 프로젝트를 맡게 된 김민수입니다.', 'I''m Minsu Kim, and I''ll be in charge of this project.', 'Useful Korean expression for business Korean situations.', 'Use it when introducing your role in a meeting.'),
    (1004, '그 부분은 제가 다시 확인해 보겠습니다.', 'I''ll check that part again.', 'Useful Korean expression for business Korean situations.', 'Good sentence for polite follow-up at work.'),
    (1005, '이 근처에 지하철역이 어디예요?', 'Where is the subway station around here?', 'Useful Korean expression for travel situations.', 'Practice with natural intonation for asking directions.'),
    (1006, '서울역까지 한 장 주세요.', 'One ticket to Seoul Station, please.', 'Useful Korean expression for travel situations.', 'Memorize this as a station ticket pattern.');

INSERT INTO clip_learning_vocabulary (clip_id, word, meaning, display_order)
VALUES
    (1001, '한 잔', 'one cup', 1),
    (1002, '매장', 'store / shop', 1),
    (1003, '맡다', 'to take charge of', 1),
    (1004, '확인하다', 'to check', 1),
    (1005, '근처', 'nearby', 1),
    (1006, '한 장', 'one ticket / one sheet', 1);
