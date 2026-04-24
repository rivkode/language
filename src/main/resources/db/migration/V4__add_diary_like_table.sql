CREATE TABLE diary_like (
    id BIGINT NOT NULL AUTO_INCREMENT,
    diary_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_diary_like UNIQUE (diary_id, user_id),
    CONSTRAINT FK_diary_like_diary FOREIGN KEY (diary_id) REFERENCES diary(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_diary_like_user ON diary_like (user_id, diary_id);
