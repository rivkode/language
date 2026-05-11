ALTER TABLE chat_room
    ADD COLUMN context_type VARCHAR(50) NOT NULL DEFAULT 'GENERAL' AFTER persona_type,
    ADD COLUMN video_id VARCHAR(100) NULL AFTER context_type;

CREATE INDEX idx_chat_room_user_context ON chat_room (user_id, context_type);
