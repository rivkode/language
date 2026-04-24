CREATE TABLE diary_chat_room (
    id BIGINT NOT NULL AUTO_INCREMENT,
    diary_id BIGINT NOT NULL,
    host_user_id BIGINT NOT NULL,
    ai_assistant_enabled BIT NOT NULL DEFAULT b'1',
    participant_count INT NOT NULL DEFAULT 0,
    last_activity_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_diary_chat_room_diary UNIQUE (diary_id),
    CONSTRAINT FK_diary_chat_room_diary FOREIGN KEY (diary_id) REFERENCES diary(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE diary_chat_participant (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_host BIT NOT NULL DEFAULT b'0',
    joined_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_diary_chat_participant UNIQUE (room_id, user_id),
    CONSTRAINT FK_diary_chat_participant_room FOREIGN KEY (room_id) REFERENCES diary_chat_room(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_diary_chat_participant_user ON diary_chat_participant (user_id);

CREATE TABLE diary_chat_message (
    id BIGINT NOT NULL AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    author_user_id BIGINT NULL,
    text VARCHAR(2000) NOT NULL,
    audio_url VARCHAR(1000) NULL,
    source VARCHAR(10) NOT NULL,
    event_type VARCHAR(30) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT FK_diary_chat_message_room FOREIGN KEY (room_id) REFERENCES diary_chat_room(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_diary_chat_message_room_id ON diary_chat_message (room_id, id);
