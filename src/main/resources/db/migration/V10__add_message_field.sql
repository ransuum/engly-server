ALTER TABLE messages
ADD image_url VARCHAR(255);

ALTER TABLE messages ALTER COLUMN content DROP NOT NULL;
ALTER TABLE messages ADD CONSTRAINT content_constraint CHECK (content IS NOT NULL OR image_url IS NOT NULL);