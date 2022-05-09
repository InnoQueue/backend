package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.Queue
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DatabaseRepository : CrudRepository<Queue, Long> {

    @Query(
        value = "drop table if exists \"user\" cascade;\n" +
                "DROP SEQUENCE IF EXISTS user_id_seq;\n" +
                "drop table if exists notifications cascade;\n" +
                "DROP SEQUENCE IF EXISTS notifications_id_seq;\n" +
                "drop table if exists user_settings cascade;\n" +
                "DROP SEQUENCE IF EXISTS user_settings_id_seq;\n" +
                "drop table if exists queue cascade;\n" +
                "DROP SEQUENCE IF EXISTS queue_id_seq;\n" +
                "drop table if exists user_queue cascade;\n" +
                "DROP SEQUENCE IF EXISTS user_queue_id_seq;\n" +
                "drop table if exists queue_pin_code cascade;\n" +
                "DROP SEQUENCE IF EXISTS queue_pin_code2_id_seq;\n" +
                "drop table if exists queue_qr_code cascade;\n" +
                "DROP SEQUENCE IF EXISTS queue_qr_code2_id_seq;\n" +
                "\n" +
                "\n" +
                "\n" +
                "create table \"user\"\n" +
                "(\n" +
                "    user_id   bigserial\n" +
                "        constraint user_pk\n" +
                "            primary key,\n" +
                "    token     varchar(128) not null,\n" +
                "    name      varchar(64)  not null,\n" +
                "    fcm_token varchar(256) not null\n" +
                ");\n" +
                "\n" +
                "create unique index user_user_id_uindex\n" +
                "    on \"user\" (user_id);\n" +
                "\n" +
                "create unique index user_token_uindex\n" +
                "    on \"user\" (token);\n" +
                "\n" +
                "CREATE SEQUENCE user_id_seq START WITH 100 INCREMENT BY 1;\n" +
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
                "    completed        boolean not null,\n" +
                "    skipped          boolean not null,\n" +
                "    joined_queue     boolean not null,\n" +
                "    \"freeze\"         boolean not null,\n" +
                "    left_queue       boolean not null,\n" +
                "    your_turn        boolean not null\n" +
                ");\n" +
                "\n" +
                "create unique index user_settings_user_id_uindex\n" +
                "    on user_settings (user_id);\n" +
                "\n" +
                "create unique index user_settings_user_settings_id_uindex\n" +
                "    on user_settings (user_settings_id);\n" +
                "\n" +
                "CREATE SEQUENCE user_settings_id_seq START WITH 100 INCREMENT BY 1;\n" +
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
                "CREATE SEQUENCE queue_id_seq START WITH 100 INCREMENT BY 1;\n" +
                "\n" +
                "\n" +
                "create table notifications\n" +
                "(\n" +
                "    notification_id bigserial\n" +
                "        constraint notifications_pk\n" +
                "            primary key,\n" +
                "    user_id         bigint      not null\n" +
                "        constraint user_id\n" +
                "            references \"user\"\n" +
                "            on update cascade on delete cascade,\n" +
                "    message_type    varchar(32) not null,\n" +
                "    participant_id  bigint      not null,\n" +
                "    queue_id        bigint      not null,\n" +
                "    is_read         boolean     not null,\n" +
                "    date            timestamp   not null\n" +
                ");\n" +
                "\n" +
                "create unique index notifications_notification_id_uindex\n" +
                "    on notifications (notification_id);\n" +
                "\n" +
                "create index notifications_date__index\n" +
                "    on notifications (date);\n" +
                "\n" +
                "CREATE SEQUENCE notifications_id_seq START WITH 100 INCREMENT BY 1;\n" +
                "\n" +
                "\n" +
                "create table user_queue\n" +
                "(\n" +
                "    user_queue_id bigserial\n" +
                "        constraint user_queue_pk\n" +
                "            primary key,\n" +
                "    queue_id      bigint           not null\n" +
                "        constraint queue_id\n" +
                "            references queue\n" +
                "            on update cascade on delete cascade,\n" +
                "    user_id       bigint           not null\n" +
                "        constraint user_id\n" +
                "            references \"user\"\n" +
                "            on update cascade on delete cascade,\n" +
                "    is_active     boolean          not null,\n" +
                "    skips         integer          not null,\n" +
                "    expenses      double precision not null,\n" +
                "    is_important  boolean          not null,\n" +
                "    date_joined   timestamp        not null\n" +
                ");\n" +
                "\n" +
                "create unique index user_queue_user_queue_id_uindex\n" +
                "    on user_queue (user_queue_id);\n" +
                "\n" +
                "create index user_queue_date_joined__index\n" +
                "    on user_queue (date_joined);\n" +
                "\n" +
                "CREATE SEQUENCE user_queue_id_seq START WITH 100 INCREMENT BY 1;\n" +
                "\n" +
                "\n" +
                "create table queue_pin_code\n" +
                "(\n" +
                "    queue_id     bigint     not null\n" +
                "        constraint queue_id\n" +
                "            references queue\n" +
                "            on update cascade on delete cascade,\n" +
                "    pin_code     varchar(8) not null,\n" +
                "    id           bigserial\n" +
                "        constraint queue_pin_code_pk\n" +
                "            primary key,\n" +
                "    date_created timestamp  not null\n" +
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
                "create index queue_pin_code_date_created__index\n" +
                "    on queue_pin_code (date_created);\n" +
                "\n" +
                "CREATE SEQUENCE queue_pin_code2_id_seq START WITH 100 INCREMENT BY 1;\n" +
                "\n" +
                "\n" +
                "create table queue_qr_code\n" +
                "(\n" +
                "    id           bigserial\n" +
                "        constraint queue_qr_code_pk\n" +
                "            primary key,\n" +
                "    queue_id     bigint      not null\n" +
                "        constraint queue_id\n" +
                "            references queue\n" +
                "            on update cascade on delete cascade,\n" +
                "    qr_code      varchar(64) not null,\n" +
                "    date_created timestamp   not null\n" +
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
                "create index queue_qr_code_date_created__index\n" +
                "    on queue_qr_code (date_created);\n" +
                "\n" +
                "\n" +
                "CREATE SEQUENCE queue_qr_code2_id_seq START WITH 100 INCREMENT BY 1;\n" +
                "\n" +
                "\n" +
                "INSERT INTO public.\"user\" (user_id, token, name, fcm_token)\n" +
                "VALUES (1, '11111', 'admin', '11111');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name, fcm_token)\n" +
                "VALUES (2, '22222', 'Emil', '22222');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name, fcm_token)\n" +
                "VALUES (3, '33333', 'Roman', '33333');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name, fcm_token)\n" +
                "VALUES (4, '44444', 'Timur', '44444');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name, fcm_token)\n" +
                "VALUES (5, '55555', 'Ivan', '55555');\n" +
                "\n" +
                "\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, completed, skipped, joined_queue, \"freeze\", left_queue,\n" +
                "                                  your_turn)\n" +
                "VALUES (1, 1, true, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, completed, skipped, joined_queue, \"freeze\", left_queue,\n" +
                "                                  your_turn)\n" +
                "VALUES (2, 2, true, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, completed, skipped, joined_queue, \"freeze\", left_queue,\n" +
                "                                  your_turn)\n" +
                "VALUES (3, 3, true, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, completed, skipped, joined_queue, \"freeze\", left_queue,\n" +
                "                                  your_turn)\n" +
                "VALUES (4, 4, true, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, completed, skipped, joined_queue, \"freeze\", left_queue,\n" +
                "                                  your_turn)\n" +
                "VALUES (5, 5, true, true, true, true, true, true);\n" +
                "\n" +
                "\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (6, 'Buy Toilet Paper', 'RED', 2, true, 2);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (34, 'Buy Dishwashing Soap', 'GREEN', 1, true, 3);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (39, 'Trash', 'YELLOW', 2, false, 1);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (40, 'Buy Soap', 'ORANGE', 1, true, 1);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (44, 'Bring Water', 'BLUE', 1, false, 1);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id)\n" +
                "VALUES (46, 'Buy Sponge', 'PURPLE', 1, true, 4);\n" +
                "\n" +
                "\n" +
                "INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)\n" +
                "VALUES (1, 1, 'JOINED_QUEUE', 5, 44, true, current_timestamp + INTERVAL '- 10 DAY');\n" +
                "INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)\n" +
                "VALUES (2, 1, 'SKIPPED', 3, 40, true, current_timestamp + INTERVAL '- 9 DAY');\n" +
                "INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)\n" +
                "VALUES (3, 1, 'COMPLETED', 2, 44, true, current_timestamp + INTERVAL '- 8 DAY');\n" +
                "INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)\n" +
                "VALUES (4, 1, 'COMPLETED', 5, 39, false, current_timestamp + INTERVAL '- 7 DAY');\n" +
                "INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)\n" +
                "VALUES (5, 1, 'YOUR_TURN', 1, 39, false, current_timestamp + INTERVAL '- 7 DAY 2 hour');\n" +
                "INSERT INTO public.notifications (notification_id, user_id, message_type, participant_id, queue_id, is_read, date)\n" +
                "VALUES (6, 1, 'SHOOK', 1, 39, false, current_timestamp + INTERVAL '- 1 DAY 1 hour');\n" +
                "\n" +
                "\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (1, 44, 1, true, 0, 0, false, '2022-05-09 10:51:18.312000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (28, 46, 5, false, 0, 50.6, false, '2022-05-09 11:07:59.745000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (6, 39, 2, true, 0, 0, false, '2022-05-09 10:57:19.938000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (18, 39, 3, true, 0, 0, false, '2022-05-09 11:05:21.981000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (13, 44, 3, true, 0, 0, false, '2022-05-09 11:04:36.458000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (19, 44, 4, true, 0, 0, false, '2022-05-09 11:06:00.226000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (7, 44, 2, true, 0, 0, false, '2022-05-09 11:01:44.692000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (24, 39, 4, true, 0, 0, false, '2022-05-09 11:06:39.121000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (30, 39, 5, true, 0, 0, false, '2022-05-09 11:08:14.344000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (12, 39, 1, true, 0, 0, true, '2022-05-09 11:03:41.915000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (4, 34, 1, false, 0, 50, false, '2022-05-09 10:52:22.417000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (3, 46, 1, false, 0, 45, false, '2022-05-09 10:51:58.522000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (25, 44, 5, false, 0, 0, false, '2022-05-09 11:07:28.740000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (8, 34, 2, true, 0, 120.87, false, '2022-05-09 11:01:54.188000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (10, 46, 2, true, 0, 47.59, false, '2022-05-09 11:02:14.258000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (16, 46, 3, true, 0, 41.7, false, '2022-05-09 11:05:03.947000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (17, 6, 3, true, 0, 198.4, false, '2022-05-09 11:05:12.865000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (5, 6, 2, true, 0, 219.89, false, '2022-05-09 10:57:02.583000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (23, 6, 4, true, 0, 187.68, false, '2022-05-09 11:06:31.234000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (11, 6, 1, true, 0, 200, false, '2022-05-09 11:03:33.380000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (29, 6, 5, true, 0, 224.7, false, '2022-05-09 11:08:07.399000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (14, 34, 3, true, 0, 234.6, false, '2022-05-09 11:04:44.603000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (26, 34, 5, false, 0, 321.87, false, '2022-05-09 11:07:39.780000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (20, 34, 4, true, 0, 123.45, false, '2022-05-09 11:06:07.441000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (27, 40, 5, true, 0, 123.45, false, '2022-05-09 11:07:49.280000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (15, 40, 3, true, 0, 100, false, '2022-05-09 11:04:53.193000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (21, 40, 4, true, 0, 90, false, '2022-05-09 11:06:16.359000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (9, 40, 2, true, 0, 120, false, '2022-05-09 11:02:04.205000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (2, 40, 1, true, 0, 110, false, '2022-05-09 10:51:31.469000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined)\n" +
                "VALUES (22, 46, 4, true, 0, 58.09, false, '2022-05-09 11:06:24.098000');\n",
        nativeQuery = true
    )
    fun resetDB()

}
