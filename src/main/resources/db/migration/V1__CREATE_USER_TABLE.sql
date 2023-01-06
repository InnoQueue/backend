CREATE TABLE IF NOT EXISTS "user"
(
    user_id      BIGSERIAL PRIMARY KEY,
    token        VARCHAR(128) NOT NULL,
    name         VARCHAR(64)  NOT NULL,
    completed    BOOLEAN      NOT NULL DEFAULT TRUE,
    skipped      BOOLEAN      NOT NULL DEFAULT TRUE,
    joined_queue BOOLEAN      NOT NULL DEFAULT TRUE,
    "freeze"     BOOLEAN      NOT NULL DEFAULT TRUE,
    left_queue   BOOLEAN      NOT NULL DEFAULT TRUE,
    your_turn    BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE SEQUENCE IF NOT EXISTS user_id_seq START WITH 100 INCREMENT BY 1;
