CREATE TABLE diary_comment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    diary_id BIGINT NOT NULL,
    author_user_id BIGINT NOT NULL,
    text VARCHAR(500) NOT NULL,
    parent_comment_id BIGINT NULL,
    like_count INT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT FK_diary_comment_diary FOREIGN KEY (diary_id) REFERENCES diary(id) ON DELETE CASCADE,
    CONSTRAINT FK_diary_comment_author FOREIGN KEY (author_user_id) REFERENCES `user`(id)
) ENGINE=InnoDB;

CREATE INDEX idx_comment_diary_created ON diary_comment (diary_id, created_at, id);
CREATE INDEX idx_comment_parent ON diary_comment (parent_comment_id);

CREATE TABLE comment_like (
    id BIGINT NOT NULL AUTO_INCREMENT,
    comment_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_comment_like UNIQUE (comment_id, user_id),
    CONSTRAINT FK_comment_like_comment FOREIGN KEY (comment_id) REFERENCES diary_comment(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_comment_like_user ON comment_like (user_id, comment_id);
