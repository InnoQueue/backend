drop table if exists "user" cascade;
DROP SEQUENCE IF EXISTS user_id_seq;
drop table if exists notifications cascade;
DROP SEQUENCE IF EXISTS notifications_id_seq;
drop table if exists user_settings cascade;
DROP SEQUENCE IF EXISTS user_settings_id_seq;
drop table if exists queue cascade;
DROP SEQUENCE IF EXISTS queue_id_seq;
drop table if exists user_queue cascade;
DROP SEQUENCE IF EXISTS user_queue_id_seq;
drop table if exists queue_pin_code cascade;
DROP SEQUENCE IF EXISTS queue_pin_code2_id_seq;
drop table if exists queue_qr_code cascade;
DROP SEQUENCE IF EXISTS queue_qr_code2_id_seq;



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

CREATE SEQUENCE user_id_seq START WITH 100 INCREMENT BY 1;


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

CREATE SEQUENCE user_settings_id_seq START WITH 100 INCREMENT BY 1;


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

CREATE SEQUENCE queue_id_seq START WITH 100 INCREMENT BY 1;


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

CREATE SEQUENCE notifications_id_seq START WITH 100 INCREMENT BY 1;


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

CREATE SEQUENCE user_queue_id_seq START WITH 100 INCREMENT BY 1;


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

CREATE SEQUENCE queue_pin_code2_id_seq START WITH 100 INCREMENT BY 1;


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


CREATE SEQUENCE queue_qr_code2_id_seq START WITH 100 INCREMENT BY 1;


INSERT INTO public."user" (user_id, token, name, fcm_token)
VALUES (1, '11111', 'admin', '11111');
INSERT INTO public."user" (user_id, token, name, fcm_token)
VALUES (2, '22222', 'Emil', '22222');
INSERT INTO public."user" (user_id, token, name, fcm_token)
VALUES (3, '33333', 'Roman', '33333');
INSERT INTO public."user" (user_id, token, name, fcm_token)
VALUES (4, '44444', 'Timur', '44444');
INSERT INTO public."user" (user_id, token, name, fcm_token)
VALUES (5, '55555', 'Ivan', '55555');


INSERT INTO public.user_settings (user_settings_id, user_id, completed, skipped, joined_queue, "freeze", left_queue,
                                  your_turn)
VALUES (1, 1, true, true, true, true, true, true);
INSERT INTO public.user_settings (user_settings_id, user_id, completed, skipped, joined_queue, "freeze", left_queue,
                                  your_turn)
VALUES (2, 2, true, true, true, true, true, true);
INSERT INTO public.user_settings (user_settings_id, user_id, completed, skipped, joined_queue, "freeze", left_queue,
                                  your_turn)
VALUES (3, 3, true, true, true, true, true, true);
INSERT INTO public.user_settings (user_settings_id, user_id, completed, skipped, joined_queue, "freeze", left_queue,
                                  your_turn)
VALUES (4, 4, true, true, true, true, true, true);
INSERT INTO public.user_settings (user_settings_id, user_id, completed, skipped, joined_queue, "freeze", left_queue,
                                  your_turn)
VALUES (5, 5, true, true, true, true, true, true);


INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (6, 'Buy Toilet Paper', 'RED', 2, true, 2);
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (34, 'Buy Dishwashing Soap', 'GREEN', 1, true, 3);
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (39, 'Trash', 'YELLOW', 2, false, 1);
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (40, 'Buy Soap', 'ORANGE', 1, true, 1);
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (44, 'Bring Water', 'BLUE', 1, false, 1);
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (46, 'Buy Sponge', 'PURPLE', 1, true, 4);


INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (1, 1, 'JOINED_QUEUE', 5, 44, true, current_timestamp + INTERVAL '- 10 DAY');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (2, 1, 'SKIPPED', 3, 40, true, current_timestamp + INTERVAL '- 9 DAY');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (3, 1, 'COMPLETED', 2, 44, true, current_timestamp + INTERVAL '- 8 DAY');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (4, 1, 'COMPLETED', 5, 39, false, current_timestamp + INTERVAL '- 7 DAY');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (5, 1, 'YOUR_TURN', 1, 39, false, current_timestamp + INTERVAL '- 7 DAY 2 hour');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (6, 1, 'SHOOK', 1, 39, false, current_timestamp + INTERVAL '- 1 DAY 1 hour');


INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (1, 44, 1, true, 0, 0, false, '2022-05-09 10:51:18.312000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (28, 46, 5, false, 0, 50.6, false, '2022-05-09 11:07:59.745000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (6, 39, 2, true, 0, 0, false, '2022-05-09 10:57:19.938000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (18, 39, 3, true, 0, 0, false, '2022-05-09 11:05:21.981000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (13, 44, 3, true, 0, 0, false, '2022-05-09 11:04:36.458000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (19, 44, 4, true, 0, 0, false, '2022-05-09 11:06:00.226000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (7, 44, 2, true, 0, 0, false, '2022-05-09 11:01:44.692000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (24, 39, 4, true, 0, 0, false, '2022-05-09 11:06:39.121000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (30, 39, 5, true, 0, 0, false, '2022-05-09 11:08:14.344000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (12, 39, 1, true, 0, 0, true, '2022-05-09 11:03:41.915000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (4, 34, 1, false, 0, 50, false, '2022-05-09 10:52:22.417000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (3, 46, 1, false, 0, 45, false, '2022-05-09 10:51:58.522000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (25, 44, 5, false, 0, 0, false, '2022-05-09 11:07:28.740000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (8, 34, 2, true, 0, 120.87, false, '2022-05-09 11:01:54.188000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (10, 46, 2, true, 0, 47.59, false, '2022-05-09 11:02:14.258000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (16, 46, 3, true, 0, 41.7, false, '2022-05-09 11:05:03.947000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (17, 6, 3, true, 0, 198.4, false, '2022-05-09 11:05:12.865000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (5, 6, 2, true, 0, 219.89, false, '2022-05-09 10:57:02.583000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (23, 6, 4, true, 0, 187.68, false, '2022-05-09 11:06:31.234000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (11, 6, 1, true, 0, 200, false, '2022-05-09 11:03:33.380000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (29, 6, 5, true, 0, 224.7, false, '2022-05-09 11:08:07.399000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (14, 34, 3, true, 0, 234.6, false, '2022-05-09 11:04:44.603000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (26, 34, 5, false, 0, 321.87, false, '2022-05-09 11:07:39.780000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (20, 34, 4, true, 0, 123.45, false, '2022-05-09 11:06:07.441000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (27, 40, 5, true, 0, 123.45, false, '2022-05-09 11:07:49.280000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (15, 40, 3, true, 0, 100, false, '2022-05-09 11:04:53.193000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (21, 40, 4, true, 0, 90, false, '2022-05-09 11:06:16.359000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (9, 40, 2, true, 0, 120, false, '2022-05-09 11:02:04.205000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (2, 40, 1, true, 0, 110, false, '2022-05-09 10:51:31.469000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (22, 46, 4, true, 0, 58.09, false, '2022-05-09 11:06:24.098000');
