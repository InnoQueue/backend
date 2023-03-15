CREATE TABLE IF NOT EXISTS notification
(
    notification_id  BIGSERIAL PRIMARY KEY,
    user_id          BIGSERIAL REFERENCES "user" (user_id) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
    queue_id         BIGINT                                                                    REFERENCES "queue" (queue_id) ON UPDATE CASCADE ON DELETE SET NULL,
    participant_id   BIGINT                                                                    REFERENCES "user" (user_id) ON UPDATE CASCADE ON DELETE SET NULL,
    queue_name       VARCHAR(64),
    participant_name VARCHAR(64),
    message_type     VARCHAR(32)                                                               NOT NULL,
    is_read          BOOLEAN                                                                   NOT NULL DEFAULT FALSE,
    date             TIMESTAMP                                                                 NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS notification_id_seq START WITH 100 INCREMENT BY 1;
