ALTER TABLE queue
    ADD COLUMN IF NOT EXISTS is_important boolean NOT NULL default false;
