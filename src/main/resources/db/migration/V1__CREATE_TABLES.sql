create table "user"
(
    user_id   bigserial
        constraint user_pk
            primary key,
    token     varchar(128) not null,
    name      varchar(64)  not null,
    fcm_token varchar(256) not null
);

create unique index user_user_id_uindex
    on "user" (user_id);

create unique index user_token_uindex
    on "user" (token);


create table user_settings
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

create unique index user_settings_user_id_uindex
    on user_settings (user_id);

create unique index user_settings_user_settings_id_uindex
    on user_settings (user_settings_id);


create table queue
(
    queue_id        bigserial
        constraint queue_pk
            primary key,
    name            varchar(64) not null,
    color           varchar(64) not null,
    creator_id      bigint      not null
        constraint creator_id
            references "user"
            on update cascade on delete cascade,
    track_expenses  boolean     not null,
    current_user_id bigint      not null
        constraint current_user_id
            references "user"
            on update cascade on delete cascade
);

create unique index queue_queue_id_uindex
    on queue (queue_id);


create table notifications
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

create unique index notifications_notification_id_uindex
    on notifications (notification_id);

create index notifications_date__index
    on notifications (date);


create table user_queue
(
    user_queue_id bigserial
        constraint user_queue_pk
            primary key,
    queue_id      bigint           not null
        constraint queue_id
            references queue
            on update cascade on delete cascade,
    user_id       bigint           not null
        constraint user_id
            references "user"
            on update cascade on delete cascade,
    is_active     boolean          not null,
    skips         integer          not null,
    expenses      double precision not null,
    is_important  boolean          not null,
    date_joined   timestamp        not null
);

create unique index user_queue_user_queue_id_uindex
    on user_queue (user_queue_id);

create index user_queue_date_joined__index
    on user_queue (date_joined);


create table queue_pin_code
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

create unique index queue_pin_code_id_uindex
    on queue_pin_code (id);

create unique index queue_pin_code_queue_id_uindex
    on queue_pin_code (queue_id);

create unique index queue_pin_code_pin_code_uindex
    on queue_pin_code (pin_code);

create index queue_pin_code_date_created__index
    on queue_pin_code (date_created);


create table queue_qr_code
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

create unique index queue_qr_code_id_uindex
    on queue_qr_code (id);

create unique index queue_qr_code_queue_id_uindex
    on queue_qr_code (queue_id);

create unique index queue_qr_code_qr_code_uindex
    on queue_qr_code (qr_code);

create index queue_qr_code_date_created__index
    on queue_qr_code (date_created);
