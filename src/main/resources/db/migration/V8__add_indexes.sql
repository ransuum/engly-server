-- Performance indexes
CREATE INDEX idx_rooms_category_created ON rooms (category_id, created_at DESC);
CREATE INDEX idx_rooms_created ON rooms (created_at DESC);
CREATE INDEX idx_rooms_name ON rooms (name);
CREATE INDEX idx_rooms_name_category ON rooms (name, category_id);
CREATE INDEX idx_rooms_category ON rooms (category_id);
CREATE INDEX idx_rooms_creator ON rooms (creator_id);

-- Ultra-fast message indexes
CREATE INDEX idx_messages_room_deleted_created ON messages(room_id, is_deleted, created_at DESC);
CREATE INDEX idx_chat_participants_room ON chat_participants(room_id) WHERE leave_at IS NULL;

CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX idx_rooms_text_search ON rooms USING gin((name || ' ' || COALESCE(description, '')) gin_trgm_ops);
CREATE INDEX idx_messages_content_trgm ON messages USING gin(content gin_trgm_ops);

CREATE INDEX idx_message_reads_message_read_at ON message_reads(message_id, read_at DESC);