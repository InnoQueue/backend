CREATE TABLE IF NOT EXISTS user_queue
(
    user_id     BIGSERIAL REFERENCES "user" (user_id) ON UPDATE CASCADE ON DELETE CASCADE   NOT NULL,
    queue_id    BIGSERIAL REFERENCES "queue" (queue_id) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
    progress    INTEGER                                                                     NOT NULL DEFAULT 0,
    completes   INTEGER                                                                     NOT NULL DEFAULT 0 CHECK (completes >= 0),
    skips       INTEGER                                                                     NOT NULL DEFAULT 0 CHECK (skips >= 0),
    expenses    BIGINT                                                                      NOT NULL DEFAULT 0 CHECK (expenses >= 0),
    is_active   BOOLEAN                                                                     NOT NULL DEFAULT TRUE,
    date_joined TIMESTAMP                                                                   NOT NULL,
    PRIMARY KEY (user_id, queue_id)
);
