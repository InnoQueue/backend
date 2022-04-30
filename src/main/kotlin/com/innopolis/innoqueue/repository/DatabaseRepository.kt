package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.Queue
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DatabaseRepository : CrudRepository<Queue, Long> {

    @Query(
        value = "drop table if exists \"user\" cascade;\n" +
                "drop table if exists user_notifications cascade;\n" +
                "drop table if exists user_settings cascade;\n" +
                "drop table if exists queue cascade;\n" +
                "drop table if exists user_queue cascade;\n" +
                "drop table if exists queue_pin_code cascade;\n" +
                "drop table if exists queue_qr_code cascade;\n" +
                "\n" +
                "\n" +
                "\n" +
                "create table \"user\"\n" +
                "(\n" +
                "    user_id bigserial\n" +
                "        constraint user_pk\n" +
                "            primary key,\n" +
                "    token   varchar(128) not null,\n" +
                "    name    varchar(64)  not null\n" +
                ");\n" +
                "\n" +
                "create unique index user_user_id_uindex\n" +
                "    on \"user\" (user_id);\n" +
                "\n" +
                "create unique index user_token_uindex\n" +
                "    on \"user\" (token);\n" +
                "\n" +
                "\n" +
                "create table user_notifications\n" +
                "(\n" +
                "    notification_id bigserial\n" +
                "        constraint user_notifications_pk\n" +
                "            primary key,\n" +
                "    user_id         bigint       not null\n" +
                "        constraint user_id\n" +
                "            references \"user\"\n" +
                "            on update cascade on delete cascade,\n" +
                "    message         varchar(256) not null,\n" +
                "    date            timestamp    not null,\n" +
                "    is_read         boolean      not null\n" +
                ");\n" +
                "\n" +
                "\n" +
                "create unique index user_notifications_notification_id_uindex\n" +
                "    on user_notifications (notification_id);\n" +
                "\n" +
                "\n" +
                "create table user_settings\n" +
                "(\n" +
                "    user_settings_id bigserial\n" +
                "        constraint user_settings_pk\n" +
                "            primary key,\n" +
                "    user_id          bigint  not null\n" +
                "        constraint user_id\n" +
                "            references \"user\"\n" +
                "            on update cascade on delete cascade,\n" +
                "    n1               boolean not null,\n" +
                "    n2               boolean not null,\n" +
                "    n3               boolean not null,\n" +
                "    n4               boolean not null,\n" +
                "    n5               boolean not null\n" +
                ");\n" +
                "\n" +
                "create unique index user_settings_user_id_uindex\n" +
                "    on user_settings (user_id);\n" +
                "\n" +
                "create unique index user_settings_user_settings_id_uindex\n" +
                "    on user_settings (user_settings_id);\n" +
                "\n" +
                "\n" +
                "create table queue\n" +
                "(\n" +
                "    queue_id        bigserial\n" +
                "        constraint queue_pk\n" +
                "            primary key,\n" +
                "    name            varchar(64) not null,\n" +
                "    color           varchar(64) not null,\n" +
                "    creator_id      bigint      not null\n" +
                "        constraint creator_id\n" +
                "            references \"user\"\n" +
                "            on update cascade on delete cascade,\n" +
                "    track_expenses  boolean     not null,\n" +
                "    current_user_id bigint      not null\n" +
                "        constraint current_user_id\n" +
                "            references \"user\"\n" +
                "            on update cascade on delete cascade\n" +
                ");\n" +
                "\n" +
                "create unique index queue_queue_id_uindex\n" +
                "    on queue (queue_id);\n" +
                "\n" +
                "\n" +
                "create table user_queue\n" +
                "(\n" +
                "    user_queue_id bigserial\n" +
                "        constraint user_queue_pk\n" +
                "            primary key,\n" +
                "    queue_id      bigint    not null\n" +
                "        constraint queue_id\n" +
                "            references queue\n" +
                "            on update cascade on delete cascade,\n" +
                "    user_id       bigint    not null\n" +
                "        constraint user_id\n" +
                "            references \"user\"\n" +
                "            on update cascade on delete cascade,\n" +
                "    is_active     boolean   not null,\n" +
                "    skips         integer   not null,\n" +
                "    expenses      integer   not null,\n" +
                "    is_important  boolean   not null,\n" +
                "    date_joined   timestamp not null\n" +
                ");\n" +
                "\n" +
                "create unique index user_queue_user_queue_id_uindex\n" +
                "    on user_queue (user_queue_id);\n" +
                "\n" +
                "\n" +
                "create table queue_pin_code\n" +
                "(\n" +
                "    queue_id bigint     not null\n" +
                "        constraint queue_id\n" +
                "            references queue\n" +
                "            on update cascade on delete cascade,\n" +
                "    pin_code varchar(8) not null,\n" +
                "    id       bigserial\n" +
                "        constraint queue_pin_code_pk\n" +
                "            primary key\n" +
                ");\n" +
                "\n" +
                "create unique index queue_pin_code_id_uindex\n" +
                "    on queue_pin_code (id);\n" +
                "\n" +
                "create unique index queue_pin_code_queue_id_uindex\n" +
                "    on queue_pin_code (queue_id);\n" +
                "\n" +
                "create unique index queue_pin_code_pin_code_uindex\n" +
                "    on queue_pin_code (pin_code);\n" +
                "\n" +
                "\n" +
                "create table queue_qr_code\n" +
                "(\n" +
                "    id       bigserial\n" +
                "        constraint queue_qr_code_pk\n" +
                "            primary key,\n" +
                "    queue_id bigint      not null\n" +
                "        constraint queue_id\n" +
                "            references queue\n" +
                "            on update cascade on delete cascade,\n" +
                "    qr_code  varchar(64) not null\n" +
                ");\n" +
                "\n" +
                "create unique index queue_qr_code_id_uindex\n" +
                "    on queue_qr_code (id);\n" +
                "\n" +
                "create unique index queue_qr_code_queue_id_uindex\n" +
                "    on queue_qr_code (queue_id);\n" +
                "\n" +
                "create unique index queue_qr_code_qr_code_uindex\n" +
                "    on queue_qr_code (qr_code);\n" +
                "\n" +
                "\n" +
                "\n" +
                "INSERT INTO public.\"user\" (user_id, token, name)\n" +
                "VALUES (15, '22222', 'Emil');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name)\n" +
                "VALUES (1, '11111', 'admin');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name)\n" +
                "VALUES (2, '2', 'Ivan');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name)\n" +
                "VALUES (5, '5', 'Alice');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name)\n" +
                "VALUES (3, '3', 'Bob');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name)\n" +
                "VALUES (4, '4', 'Peter');\n" +
                "\n" +
                "\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (10, 15, 'Bob skipped his turn in **Buy Soap**', '2022-01-12 12:34:25.000000', true);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (11, 15, 'Ivan completed **Bring Water**', '2022-01-13 11:08:38.000000', true);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (12, 15, 'You were shook by roommate to remind you that it is your turn in **Trash**!',\n" +
                "        '2022-01-20 10:01:32.000000', false);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (13, 15, 'it is now your turn in queue **Trash**', '2022-01-14 23:48:59.000000', false);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (14, 15, 'Alex joined in queue **Bring Water**', '2022-01-11 19:48:45.000000', true);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (15, 15, 'Alex completed **Trash**', '2022-01-14 23:48:55.000000', false);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (6, 1, 'Bob skipped his turn in **Buy Soap**', '2022-01-12 12:34:25.000000', true);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (5, 1, 'Ivan completed **Bring Water**', '2022-01-13 11:08:38.000000', true);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (9, 1, 'You were shook by roommate to remind you that it is your turn in **Trash**!',\n" +
                "        '2022-01-20 10:01:32.000000', false);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (7, 1, 'it is now your turn in queue **Trash**', '2022-01-14 23:48:59.000000', false);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (1, 1, 'Alex joined in queue **Bring Water**', '2022-01-11 19:48:45.000000', true);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read)\n" +
                "VALUES (8, 1, 'Alex completed **Trash**', '2022-01-14 23:48:55.000000', false);\n" +
                "\n" +
                "\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)\n" +
                "VALUES (10, 15, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)\n" +
                "VALUES (2, 2, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)\n" +
                "VALUES (3, 3, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)\n" +
                "VALUES (4, 4, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)\n" +
                "VALUES (5, 5, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5)\n" +
                "VALUES (1, 1, true, true, true, true, true);\n" +
                "\n" +
                "\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (44, 'Bring Water', 'BLUE', 1, false, 1);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (40, 'Buy Soap', 'ORANGE', 1, true, 1);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (46, 'Buy Sponge', 'PURPLE', 1, true, 15);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (6, 'Buy Toilet Paper', 'RED', 15, true, 15);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (39, 'Trash', 'YELLOW', 15, false, 1);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (34, 'Buy Dishwashing Soap', 'GREEN', 1, true, 15);\n" +
                "\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (19, 44, 15, true, 0, 0, false, '2022-04-30 20:11:05.312758');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (20, 40, 15, true, 0, 0, false, '2022-04-30 20:11:11.922780');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (22, 6, 15, true, 0, 0, false, '2022-04-30 20:11:39.530443');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (23, 39, 15, true, 0, 0, false, '2022-04-30 20:11:48.209418');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (24, 34, 15, true, 0, 0, false, '2022-04-30 20:11:55.648157');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (25, 46, 15, true, 0, 0, false, '2022-04-30 20:12:03.499760');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (60, 39, 2, true, 0, 0, false, '2022-03-26 15:37:27.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (62, 40, 2, true, 0, 0, false, '2022-03-26 15:37:49.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (67, 44, 1, true, 0, 0, false, '2022-03-26 12:46:48.649000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (10, 6, 1, true, 0, 0, false, '2022-03-02 22:36:56.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (71, 6, 3, true, 0, 0, false, '2022-03-26 16:03:28.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (72, 6, 4, true, 0, 0, false, '2022-03-26 16:03:29.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (73, 6, 5, true, 0, 0, false, '2022-03-26 16:03:31.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (74, 34, 2, true, 0, 0, false, '2022-03-26 16:04:19.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (21, 6, 2, true, 0, 0, false, '2022-03-25 01:32:42.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (75, 34, 3, true, 0, 0, false, '2022-03-26 16:04:21.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (76, 34, 4, true, 0, 0, false, '2022-03-26 16:04:22.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (77, 34, 5, true, 0, 0, false, '2022-03-26 16:04:23.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (81, 39, 3, true, 0, 0, false, '2022-03-26 16:05:04.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (82, 39, 4, true, 0, 0, false, '2022-03-26 16:05:06.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (83, 39, 5, true, 0, 0, false, '2022-03-26 16:05:07.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (84, 40, 3, true, 0, 0, false, '2022-03-26 16:05:59.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (85, 40, 4, true, 0, 0, false, '2022-03-26 16:06:00.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (86, 40, 5, true, 0, 0, false, '2022-03-26 16:06:01.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (87, 44, 2, true, 0, 0, false, '2022-03-26 16:06:40.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (90, 44, 5, true, 0, 0, false, '2022-03-26 16:06:43.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (91, 46, 2, true, 0, 0, false, '2022-03-26 16:07:32.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (92, 46, 3, true, 0, 0, false, '2022-03-26 16:07:33.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (93, 46, 4, true, 0, 0, false, '2022-03-26 16:07:34.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (94, 46, 5, true, 0, 0, false, '2022-03-26 16:07:36.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (68, 39, 1, true, 0, 0, true, '2022-03-26 15:58:10.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (69, 40, 1, true, 0, 0, false, '2022-03-26 15:58:31.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (70, 46, 1, false, 0, 0, false, '2022-03-26 15:58:52.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (54, 34, 1, false, 0, 0, true, '2022-03-26 12:05:51.988000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (89, 44, 4, false, 0, 0, false, '2022-03-26 16:06:42.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (88, 44, 3, false, 0, 0, false, '2022-03-26 16:06:41.000000');\n",
        nativeQuery = true
    )
    fun resetDB()

}
