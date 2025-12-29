CREATE OR REPLACE FUNCTION get_rooms_with_last_message(p_category_id VARCHAR)
    RETURNS TABLE (
                      id VARCHAR(255),
                      name VARCHAR(255),
                      description VARCHAR(255),
                      last_message VARCHAR(255),
                      last_message_created_at TIMESTAMP,
                      members BIGINT
                  ) AS $$
BEGIN
    RETURN QUERY
        SELECT
            r.id,
            r.name,
            r.description,
            msg.content::VARCHAR(255) as last_message,
            msg.created_at as last_message_created_at,
            COALESCE(p.member_count, 0) as members
        FROM rooms r
                 LEFT JOIN LATERAL (
            SELECT m.content, m.created_at
            FROM messages m
            WHERE m.room_id = r.id
            ORDER BY m.created_at DESC
            LIMIT 1
            ) msg ON TRUE
                 LEFT JOIN LATERAL (
            SELECT COUNT(*) as member_count
            FROM chat_participants cp
            WHERE cp.room_id = r.id AND cp.leave_at IS NULL
            ) p ON TRUE
        WHERE p_category_id IS NULL OR r.category_id = p_category_id;
END;
$$ LANGUAGE plpgsql;