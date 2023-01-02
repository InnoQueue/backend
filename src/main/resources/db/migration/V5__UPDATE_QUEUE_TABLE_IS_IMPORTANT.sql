ALTER TABLE queue
    ADD COLUMN IF NOT EXISTS is_important boolean NOT NULL default false;
ALTER TABLE user_queue
    DROP COLUMN IF EXISTS is_important;
