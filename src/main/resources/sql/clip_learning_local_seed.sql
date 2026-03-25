SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS clip_source_video (
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

CREATE TABLE IF NOT EXISTS clip_learning_clip (
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

CREATE TABLE IF NOT EXISTS clip_learning_sentence (
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

CREATE TABLE IF NOT EXISTS clip_learning_vocabulary (
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

INSERT INTO clip_source_video (id, youtube_video_id, source_url, source_title, channel_name, thumbnail_url)
VALUES
    (
        501,
        'FB_Lh9vq4Wc',
        'https://youtube.com/watch?v=FB_Lh9vq4Wc&pp=ygUT7JWE7J2064-MIOyduO2EsOu3sA%3D%3D',
        '아이들 인터뷰 클립 1',
        'Clip Learning Samples',
        'https://img.youtube.com/vi/FB_Lh9vq4Wc/hqdefault.jpg'
    ),
    (
        601,
        'LB01v___wO4',
        'https://www.youtube.com/watch?v=LB01v___wO4&pp=ygUNYnRzIOyduO2EsOu3sA%3D%3D',
        'BTS 인터뷰 클립 1',
        'Clip Learning Samples',
        'https://img.youtube.com/vi/LB01v___wO4/hqdefault.jpg'
    ),
    (
        701,
        '_B7YxQnS25U',
        'https://www.youtube.com/watch?v=_B7YxQnS25U&pp=ygUNYnRzIOyduO2EsOu3sA%3D%3D',
        'BTS 인터뷰 클립 2',
        'Clip Learning Samples',
        'https://img.youtube.com/vi/_B7YxQnS25U/hqdefault.jpg'
    )
ON DUPLICATE KEY UPDATE
    source_url = VALUES(source_url),
    source_title = VALUES(source_title),
    channel_name = VALUES(channel_name),
    thumbnail_url = VALUES(thumbnail_url);

INSERT INTO clip_learning_clip (id, source_video_id, title, category, clip_start_ms, clip_end_ms, clip_duration_ms)
VALUES
    (1001, 501, '감정 표현 따라 말하기', 'daily-conversation', 12000, 21500, 9500),
    (1002, 501, '리액션 문장 익히기', 'daily-conversation', 24000, 33200, 9200),
    (1003, 601, '인터뷰 답변 시작하기', 'entertainment', 8500, 17100, 8600),
    (1004, 601, '짧게 의견 덧붙이기', 'entertainment', 19200, 28900, 9700),
    (1005, 701, '자연스럽게 공감하기', 'entertainment', 14100, 23600, 9500),
    (1006, 701, '부드럽게 마무리하기', 'entertainment', 25900, 35100, 9200)
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    category = VALUES(category),
    clip_duration_ms = VALUES(clip_duration_ms);

INSERT INTO clip_learning_sentence (clip_id, primary_sentence, translation, explanation_summary, usage_tip)
VALUES
    (1001, '진짜 너무 떨렸어요.', 'I was really so nervous.', '감정을 자연스럽게 표현할 때 쓸 수 있는 짧은 문장입니다.', '문장 전체를 하나의 리듬으로 따라 말하면 자연스럽습니다.'),
    (1002, '생각보다 훨씬 재미있었어요.', 'It was much more fun than I expected.', '후기나 반응을 말할 때 자주 쓰는 패턴입니다.', '생각보다 + 형용사 패턴으로 다른 표현도 바꿔 연습해 보세요.'),
    (1003, '이번 무대를 준비하면서 많이 배웠어요.', 'I learned a lot while preparing for this performance.', '인터뷰에서 경험과 배움을 말할 때 좋은 문장입니다.', '준비하면서 뒤의 동사만 바꿔 다양한 답변으로 확장할 수 있습니다.'),
    (1004, '팬분들 덕분에 끝까지 힘낼 수 있었어요.', 'Thanks to the fans, we were able to keep going until the end.', '감사와 결과를 함께 말하는 표현입니다.', '덕분에 뒤에 긍정적인 결과를 붙여 말하는 연습에 좋습니다.'),
    (1005, '그 말에 정말 공감이 됐어요.', 'I really related to what you said.', '상대 말에 공감할 때 매우 자연스럽게 쓸 수 있습니다.', '정말 공감이 됐어요를 통째로 익히면 회화에서 활용도가 높습니다.'),
    (1006, '다음에도 좋은 모습 보여드릴게요.', 'We will show you a good side of us next time too.', '마무리 인사나 다짐 표현으로 쓰기 좋습니다.', '보여드릴게요 발음을 또렷하게 끊어 연습하면 좋습니다.')
ON DUPLICATE KEY UPDATE
    primary_sentence = VALUES(primary_sentence),
    translation = VALUES(translation),
    explanation_summary = VALUES(explanation_summary),
    usage_tip = VALUES(usage_tip);

INSERT INTO clip_learning_vocabulary (clip_id, word, meaning, display_order)
VALUES
    (1001, '떨리다', 'to feel nervous', 1),
    (1002, '생각보다', 'than expected', 1),
    (1003, '준비하다', 'to prepare', 1),
    (1004, '덕분에', 'thanks to', 1),
    (1005, '공감', 'empathy / relating to something', 1),
    (1006, '모습', 'appearance / side / 모습', 1)
ON DUPLICATE KEY UPDATE
    word = VALUES(word),
    meaning = VALUES(meaning);
