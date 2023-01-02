ALTER TABLE user_queue
    DROP COLUMN IF EXISTS expenses;

ALTER TABLE user_queue
    ADD COLUMN IF NOT EXISTS expenses bigint NOT NULL DEFAULT 0;
