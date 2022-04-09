package com.innopolis.innoqueue.repository

import com.innopolis.innoqueue.model.Queue
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DatabaseRepository : CrudRepository<Queue, Long> {

    @Query(
        value = "DELETE FROM public.user_notifications\n" +
                "WHERE true;\n" +
                "\n" +
                "DELETE FROM public.user_settings\n" +
                "WHERE true;\n" +
                "\n" +
                "DELETE FROM public.user_queue\n" +
                "WHERE true;\n" +
                "\n" +
                "DELETE FROM public.queue\n" +
                "WHERE true;\n" +
                "\n" +
                "DELETE FROM public.user\n" +
                "WHERE true;\n" +
                "\n" +
                "DELETE FROM public.queue_pin_code\n" +
                "WHERE true;\n" +
                "\n" +
                "\n" +
                "INSERT INTO public.\"user\" (user_id, token, name) VALUES (1, '11111', 'Miley Cyrus');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name) VALUES (2, '2', 'Ivan');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name) VALUES (5, '5', 'Alice');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name) VALUES (3, '3', 'Bob');\n" +
                "INSERT INTO public.\"user\" (user_id, token, name) VALUES (4, '4', 'Peter');\n" +
                "\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5) VALUES (2, 2, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5) VALUES (3, 3, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5) VALUES (4, 4, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5) VALUES (5, 5, true, true, true, true, true);\n" +
                "INSERT INTO public.user_settings (user_settings_id, user_id, n1, n2, n3, n4, n5) VALUES (1, 1, true, true, true, true, true);\n" +
                "\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read) VALUES (6, 1, 'Bob skipped his turn in **Buy Soap**', '2022-01-12 12:34:25.000000', true);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read) VALUES (5, 1, 'Ivan completed **Bring Water**', '2022-01-13 11:08:38.000000', true);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read) VALUES (9, 1, 'You were shook by roommate to remind you that it is your turn in **Trash**!', '2022-01-20 10:01:32.000000', false);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read) VALUES (7, 1, 'it is now your turn in queue **Trash**', '2022-01-14 23:48:59.000000', false);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read) VALUES (1, 1, 'Alex joined in queue **Bring Water**', '2022-01-11 19:48:45.000000', true);\n" +
                "INSERT INTO public.user_notifications (notification_id, user_id, message, date, is_read) VALUES (8, 1, 'Alex completed **Trash**', '2022-01-14 23:48:55.000000', false);\n" +
                "\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, link, current_user_id) VALUES (44, 'Bring Water', 'BLUE', 1, false, 'YtAE6PWtb3vRc1Ct', 1);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, link, current_user_id) VALUES (40, 'Buy Soap', 'ORANGE', 1, true, 'fjdsldsfdsfds', 1);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, link, current_user_id) VALUES (6, 'Buy Toilet Paper', 'RED', 2, true, 'hfjd', 2);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, link, current_user_id) VALUES (39, 'Trash', 'YELLOW', 2, false, 'fdsfdsgfdsf', 1);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, link, current_user_id) VALUES (34, 'Buy Dishwashing Soap', 'GREEN', 1, true, 'WUIH51ryH5JenLP7', 2);\n" +
                "INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, link, current_user_id) VALUES (46, 'Buy Sponge', 'PURPLE', 1, true, 'fdsfdsfsd', 2);\n" +
                "\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (60, 39, 2, true, 0, 0, false, '2022-03-26 15:37:27.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (62, 40, 2, true, 0, 0, false, '2022-03-26 15:37:49.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (67, 44, 1, true, 0, 0, false, '2022-03-26 12:46:48.649000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (10, 6, 1, true, 0, 0, false, '2022-03-02 22:36:56.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (71, 6, 3, true, 0, 0, false, '2022-03-26 16:03:28.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (72, 6, 4, true, 0, 0, false, '2022-03-26 16:03:29.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (73, 6, 5, true, 0, 0, false, '2022-03-26 16:03:31.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (74, 34, 2, true, 0, 0, false, '2022-03-26 16:04:19.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (21, 6, 2, true, 0, 0, false, '2022-03-25 01:32:42.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (75, 34, 3, true, 0, 0, false, '2022-03-26 16:04:21.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (76, 34, 4, true, 0, 0, false, '2022-03-26 16:04:22.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (77, 34, 5, true, 0, 0, false, '2022-03-26 16:04:23.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (81, 39, 3, true, 0, 0, false, '2022-03-26 16:05:04.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (82, 39, 4, true, 0, 0, false, '2022-03-26 16:05:06.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (83, 39, 5, true, 0, 0, false, '2022-03-26 16:05:07.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (84, 40, 3, true, 0, 0, false, '2022-03-26 16:05:59.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (85, 40, 4, true, 0, 0, false, '2022-03-26 16:06:00.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (86, 40, 5, true, 0, 0, false, '2022-03-26 16:06:01.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (87, 44, 2, true, 0, 0, false, '2022-03-26 16:06:40.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (90, 44, 5, true, 0, 0, false, '2022-03-26 16:06:43.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (91, 46, 2, true, 0, 0, false, '2022-03-26 16:07:32.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (92, 46, 3, true, 0, 0, false, '2022-03-26 16:07:33.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (93, 46, 4, true, 0, 0, false, '2022-03-26 16:07:34.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (94, 46, 5, true, 0, 0, false, '2022-03-26 16:07:36.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (68, 39, 1, true, 0, 0, true, '2022-03-26 15:58:10.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (69, 40, 1, true, 0, 0, false, '2022-03-26 15:58:31.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (70, 46, 1, false, 0, 0, false, '2022-03-26 15:58:52.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (54, 34, 1, false, 0, 0, true, '2022-03-26 12:05:51.988000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (89, 44, 4, false, 0, 0, false, '2022-03-26 16:06:42.000000');\n" +
                "INSERT INTO public.user_queue (user_queue_id, queue_id, user_id, is_active, skips, expenses, is_important, date_joined) VALUES (88, 44, 3, false, 0, 0, false, '2022-03-26 16:06:41.000000');\n",
        nativeQuery = true
    )
    fun resetDB()

}