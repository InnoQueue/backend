CREATE TABLE IF NOT EXISTS queue
(
    queue_id         BIGSERIAL PRIMARY KEY,
    name             VARCHAR(64) NOT NULL,
    color            VARCHAR(64) NOT NULL,
    track_expenses   BOOLEAN     NOT NULL,
    is_important     BOOLEAN     NOT NULL DEFAULT FALSE,
    current_user_id  BIGINT      REFERENCES "user" (user_id) ON UPDATE CASCADE ON DELETE SET NULL,
    creator_id       BIGINT      REFERENCES "user" (user_id) ON UPDATE CASCADE ON DELETE SET NULL,
    pin_code         VARCHAR(8),
    qr_code          VARCHAR(64),
    pin_date_created TIMESTAMP,
    qr_date_created  TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS queue_id_seq START WITH 100 INCREMENT BY 1;
