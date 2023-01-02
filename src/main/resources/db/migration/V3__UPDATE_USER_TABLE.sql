ALTER TABLE "user"
    ADD COLUMN IF NOT EXISTS completed    boolean NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS skipped      boolean NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS joined_queue boolean NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS "freeze"     boolean NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS left_queue   boolean NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS your_turn    boolean NOT NULL DEFAULT true;

DROP TABLE user_settings;
