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
    user_id bigserial
        constraint user_pk
            primary key,
    token   varchar(128) not null,
    name    varchar(64)  not null
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
    n1               boolean not null,
    n2               boolean not null,
    n3               boolean not null,
    n4               boolean not null,
    n5               boolean not null
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
    participant_id  bigint
        constraint participant_id
            references "user"
            on update cascade on delete cascade,
    queue_id        bigint      not null
        constraint queue_id
            references queue
            on update cascade on delete cascade,
    is_read         boolean     not null,
    date            timestamp   not null
);

create unique index notifications_notification_id_uindex
    on notifications (notification_id);

CREATE SEQUENCE notifications_id_seq START WITH 100 INCREMENT BY 1;


create table user_queue
(
    user_queue_id bigserial
        constraint user_queue_pk
            primary key,
    queue_id      bigint    not null
        constraint queue_id
            references queue
            on update cascade on delete cascade,
    user_id       bigint    not null
        constraint user_id
            references "user"
            on update cascade on delete cascade,
    is_active     boolean   not null,
    skips         integer   not null,
    expenses      integer   not null,
    is_important  boolean   not null,
    date_joined   timestamp not null
);

create unique index user_queue_user_queue_id_uindex
    on user_queue (user_queue_id);

CREATE SEQUENCE user_queue_id_seq START WITH 100 INCREMENT BY 1;


create table queue_pin_code
(
    queue_id bigint     not null
        constraint queue_id
            references queue
            on update cascade on delete cascade,
    pin_code varchar(8) not null,
    id       bigserial
        constraint queue_pin_code_pk
            primary key
);

create unique index queue_pin_code_id_uindex
    on queue_pin_code (id);

create unique index queue_pin_code_queue_id_uindex
    on queue_pin_code (queue_id);

create unique index queue_pin_code_pin_code_uindex
    on queue_pin_code (pin_code);

CREATE SEQUENCE queue_pin_code2_id_seq START WITH 100 INCREMENT BY 1;


create table queue_qr_code
(
    id       bigserial
        constraint queue_qr_code_pk
            primary key,
    queue_id bigint      not null
        constraint queue_id
            references queue
            on update cascade on delete cascade,
    qr_code  varchar(64) not null
);

create unique index queue_qr_code_id_uindex
    on queue_qr_code (id);

create unique index queue_qr_code_queue_id_uindex
    on queue_qr_code (queue_id);

create unique index queue_qr_code_qr_code_uindex
    on queue_qr_code (qr_code);

CREATE SEQUENCE queue_qr_code2_id_seq START WITH 100 INCREMENT BY 1;


INSERT INTO public."user" (user_id, token, name)
VALUES (15, '22222', 'Emil');
INSERT INTO public."user" (user_id, token, name)
VALUES (1, '11111', 'admin');
INSERT INTO public."user" (user_id, token, name)
VALUES (2, '2', 'Ivan');
INSERT INTO public."user" (user_id, token, name)
VALUES (5, '5', 'Alice');
INSERT INTO public."user" (user_id, token, name)
VALUES (3, '3', 'Bob');
INSERT INTO public."user" (user_id, token, name)
VALUES (4, '4', 'Peter');


INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)
VALUES (10, 15, true, true, true, true, true);
INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)
VALUES (2, 2, true, true, true, true, true);
INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)
VALUES (3, 3, true, true, true, true, true);
INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)
VALUES (4, 4, true, true, true, true, true);
INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)
VALUES (5, 5, true, true, true, true, true);
INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)
VALUES (1, 1, true, true, true, true, true);


INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (44, 'Bring Water', 'BLUE', 1, false, 1);
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (40, 'Buy Soap', 'ORANGE', 1, true, 1);
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (46, 'Buy Sponge', 'PURPLE', 1, true, 15);
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (6, 'Buy Toilet Paper', 'RED', 15, true, 15);
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (39, 'Trash', 'YELLOW', 15, false, 1);
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)
VALUES (34, 'Buy Dishwashing Soap', 'GREEN', 1, true, 15);


INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (7, 15, 'SKIPPED', 3, 40, true, '2022-01-12 12:34:25.000000');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (1, 1, 'JOINED_QUEUE', 5, 44, true, '2022-01-11 19:48:45.000000');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (6, 1, 'SHOOK', 1, 39, false, '2022-01-20 10:01:32.000000');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (9, 15, 'SHOOK', 15, 39, false, '2022-01-20 10:01:32.000000');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (3, 1, 'SKIPPED', 3, 40, true, '2022-01-12 12:34:25.000000');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (12, 15, 'COMPLETED', 5, 39, false, '2022-01-14 23:48:55.000000');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (10, 15, 'YOUR_TURN', 15, 39, false, '2022-01-14 23:48:59.000000');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (8, 15, 'COMPLETED', 2, 44, true, '2022-01-13 11:08:38.000000');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (11, 15, 'JOINED_QUEUE', 5, 44, true, '2022-01-11 19:48:45.000000');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (2, 1, 'COMPLETED', 2, 44, true, '2022-01-13 11:08:38.000000');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (5, 1, 'COMPLETED', 5, 39, false, '2022-01-14 23:48:55.000000');
INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)
VALUES (4, 1, 'YOUR_TURN', 1, 39, false, '2022-01-14 23:48:59.000000');


INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (19, 44, 15, false, 0, 0, false, '2022-04-30 20:11:05.312758');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (23, 39, 15, true, 0, 0, false, '2022-04-30 20:11:48.209418');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (60, 39, 2, true, 0, 0, false, '2022-03-26 15:37:27.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (67, 44, 1, true, 0, 0, false, '2022-03-26 12:46:48.649000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (81, 39, 3, true, 0, 0, false, '2022-03-26 16:05:04.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (82, 39, 4, true, 0, 0, false, '2022-03-26 16:05:06.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (83, 39, 5, true, 0, 0, false, '2022-03-26 16:05:07.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (87, 44, 2, true, 0, 0, false, '2022-03-26 16:06:40.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (90, 44, 5, true, 0, 0, false, '2022-03-26 16:06:43.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (68, 39, 1, true, 0, 0, true, '2022-03-26 15:58:10.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (89, 44, 4, false, 0, 0, false, '2022-03-26 16:06:42.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (88, 44, 3, false, 0, 0, false, '2022-03-26 16:06:41.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (85, 40, 4, true, 0, 87, false, '2022-03-26 16:06:00.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (75, 34, 3, true, 0, 201, false, '2022-03-26 16:04:21.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (93, 46, 4, true, 0, 50, false, '2022-03-26 16:07:34.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (76, 34, 4, true, 0, 230, false, '2022-03-26 16:04:22.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (25, 46, 15, true, 0, 40, false, '2022-04-30 20:12:03.499760');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (69, 40, 1, true, 0, 92, false, '2022-03-26 15:58:31.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (54, 34, 1, false, 0, 200, true, '2022-03-26 12:05:51.988000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (73, 6, 5, true, 0, 120, false, '2022-03-26 16:03:31.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (92, 46, 3, true, 0, 25, false, '2022-03-26 16:07:33.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (72, 6, 4, true, 0, 90, false, '2022-03-26 16:03:29.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (74, 34, 2, true, 0, 210, false, '2022-03-26 16:04:19.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (21, 6, 2, true, 0, 117, false, '2022-03-25 01:32:42.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (77, 34, 5, true, 0, 190, false, '2022-03-26 16:04:23.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (22, 6, 15, true, 0, 123, false, '2022-04-30 20:11:39.530443');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (20, 40, 15, false, 0, 85, false, '2022-04-30 20:11:11.922780');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (71, 6, 3, true, 0, 100, false, '2022-03-26 16:03:28.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (94, 46, 5, true, 0, 42, false, '2022-03-26 16:07:36.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (70, 46, 1, false, 0, 47, false, '2022-03-26 15:58:52.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (62, 40, 2, true, 0, 82, false, '2022-03-26 15:37:49.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (86, 40, 5, true, 0, 90, false, '2022-03-26 16:06:01.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (91, 46, 2, true, 0, 35, false, '2022-03-26 16:07:32.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (10, 6, 1, true, 0, 140, false, '2022-03-02 22:36:56.000000');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (24, 34, 15, true, 0, 248, false, '2022-04-30 20:11:55.648157');
INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)
VALUES (84, 40, 3, true, 0, 80, false, '2022-03-26 16:05:59.000000');
