CREATE TABLE IF NOT EXISTS fcm_token
(
    user_id      BIGSERIAL REFERENCES "user" (user_id) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
    fcm_token    VARCHAR(256)                                                              NOT NULL,
    date_created TIMESTAMP                                                                 NOT NULL,
    PRIMARY KEY (user_id, fcm_token)
);
