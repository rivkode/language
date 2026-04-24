CREATE TABLE diary (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    line_1 VARCHAR(200) NOT NULL,
    line_2 VARCHAR(200) NOT NULL,
    line_3 VARCHAR(200) NOT NULL,
    is_public BIT NOT NULL DEFAULT b'1',
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT FK_diary_user FOREIGN KEY (user_id) REFERENCES `user`(id)
) ENGINE=InnoDB;

CREATE INDEX idx_diary_public_created ON diary (is_public, created_at, id);
CREATE INDEX idx_diary_user_created ON diary (user_id, created_at, id);
CREATE INDEX idx_diary_like_count ON diary (is_public, like_count, id);

CREATE TABLE diary_tag (
    id BIGINT NOT NULL AUTO_INCREMENT,
    diary_id BIGINT NOT NULL,
    tag VARCHAR(32) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT FK_diary_tag_diary FOREIGN KEY (diary_id) REFERENCES diary(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_diary_tag_diary ON diary_tag (diary_id);
CREATE INDEX idx_diary_tag_tag ON diary_tag (tag, diary_id);
