-- Idempotent: prod already has these columns/index applied manually; this
-- migration is a no-op there and creates them on fresh environments.

SET @col_context_type := (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'chat_room'
      AND COLUMN_NAME = 'context_type'
);
SET @sql := IF(@col_context_type = 0,
    'ALTER TABLE chat_room ADD COLUMN context_type VARCHAR(50) NOT NULL DEFAULT ''GENERAL'' AFTER persona_type',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_video_id := (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'chat_room'
      AND COLUMN_NAME = 'video_id'
);
SET @sql := IF(@col_video_id = 0,
    'ALTER TABLE chat_room ADD COLUMN video_id VARCHAR(100) NULL AFTER context_type',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_user_context := (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'chat_room'
      AND INDEX_NAME = 'idx_chat_room_user_context'
);
SET @sql := IF(@idx_user_context = 0,
    'CREATE INDEX idx_chat_room_user_context ON chat_room (user_id, context_type)',
    'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
