-- INSERT INTO "user" (user_id, token, name, fcm_token)
-- VALUES (1, 'token', 'user name', 'fcm token');

INSERT INTO user_settings (user_settings_id, user_id, completed, skipped, joined_queue, "freeze", left_queue, your_turn)
VALUES (1, 1, true, false, true, false, true, false);
