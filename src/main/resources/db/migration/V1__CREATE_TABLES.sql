create table IF NOT EXISTS "user"
(
    user_id   bigserial
        constraint user_pk
            primary key,
    token     varchar(128) not null,
    name      varchar(64)  not null,
    fcm_token varchar(256) not null
);

create unique index IF NOT EXISTS user_user_id_uindex
    on "user" (user_id);

create unique index IF NOT EXISTS user_token_uindex
    on "user" (token);


create table IF NOT EXISTS user_settings
(
    user_settings_id bigserial
        constraint user_settings_pk
            primary key,
    user_id          bigint  not null
        constraint user_id
            references "user"
            on update cascade on delete cascade,
    completed        boolean not null,
    skipped          boolean not null,
    joined_queue     boolean not null,
    "freeze"         boolean not null,
    left_queue       boolean not null,
    your_turn        boolean not null
);

create unique index IF NOT EXISTS user_settings_user_id_uindex
    on user_settings (user_id);

create unique index IF NOT EXISTS user_settings_user_settings_id_uindex
    on user_settings (user_settings_id);


CREATE TABLE IF NOT EXISTS queue
(
    queue_id         BIGSERIAL PRIMARY KEY,
    name             VARCHAR(64) NOT NULL,
    color            VARCHAR(64) NOT NULL,
    track_expenses   BOOLEAN     NOT NULL,
    is_important     BOOLEAN     NOT NULL DEFAULT FALSE,
    current_user_id  BIGINT,
    creator_id       BIGINT,
    pin_code         VARCHAR(8),
    qr_code          VARCHAR(64),
    pin_date_created TIMESTAMP,
    qr_date_created  TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS queue_id_seq START WITH 100 INCREMENT BY 1;


create table IF NOT EXISTS notifications
(
    notification_id bigserial
        constraint notifications_pk
            primary key,
    user_id         bigint      not null
        constraint user_id
            references "user"
            on update cascade on delete cascade,
    message_type    varchar(32) not null,
    participant_id  bigint      not null,
    queue_id        bigint      not null,
    is_read         boolean     not null,
    date            timestamp   not null
);

create unique index IF NOT EXISTS notifications_notification_id_uindex
    on notifications (notification_id);

create index IF NOT EXISTS notifications_date__index
    on notifications (date);


CREATE TABLE IF NOT EXISTS user_queue
(
    user_id     BIGSERIAL REFERENCES "user" (user_id) ON UPDATE CASCADE ON DELETE CASCADE   NOT NULL,
    queue_id    BIGSERIAL REFERENCES "queue" (queue_id) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
    progress    integer                                                                     NOT NULL DEFAULT 0,
    completes   integer                                                                     NOT NULL DEFAULT 0,
    skips       integer                                                                     NOT NULL DEFAULT 0,
    expenses    bigint                                                                      NOT NULL DEFAULT 0,
    is_active   boolean                                                                     NOT NULL DEFAULT true,
    date_joined timestamp                                                                   NOT NULL,
    PRIMARY KEY (user_id, queue_id)
);


create table IF NOT EXISTS queue_pin_code
(
    queue_id     bigint     not null
        constraint queue_id
            references queue
            on update cascade on delete cascade,
    pin_code     varchar(8) not null,
    id           bigserial
        constraint queue_pin_code_pk
            primary key,
    date_created timestamp  not null
);

create unique index IF NOT EXISTS queue_pin_code_id_uindex
    on queue_pin_code (id);

create unique index IF NOT EXISTS queue_pin_code_queue_id_uindex
    on queue_pin_code (queue_id);

create unique index IF NOT EXISTS queue_pin_code_pin_code_uindex
    on queue_pin_code (pin_code);

create index IF NOT EXISTS queue_pin_code_date_created__index
    on queue_pin_code (date_created);


create table IF NOT EXISTS queue_qr_code
(
    id           bigserial
        constraint queue_qr_code_pk
            primary key,
    queue_id     bigint      not null
        constraint queue_id
            references queue
            on update cascade on delete cascade,
    qr_code      varchar(64) not null,
    date_created timestamp   not null
);

create unique index IF NOT EXISTS queue_qr_code_id_uindex
    on queue_qr_code (id);

create unique index IF NOT EXISTS queue_qr_code_queue_id_uindex
    on queue_qr_code (queue_id);

create unique index IF NOT EXISTS queue_qr_code_qr_code_uindex
    on queue_qr_code (qr_code);

create index IF NOT EXISTS queue_qr_code_date_created__index
    on queue_qr_code (date_created);
