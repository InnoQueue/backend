INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id, pin_code,
                          pin_date_created, qr_code, qr_date_created)
VALUES (6, 'Buy Toilet Paper', 'RED', 2, true, 2, '111111', current_timestamp at time zone 'UTC', '111111',
        current_timestamp at time zone 'UTC');
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id, pin_code,
                          pin_date_created, qr_code, qr_date_created)
VALUES (34, 'Buy Dishwashing Soap', 'GREEN', 1, true, 2, '222222',
        current_timestamp at time zone 'UTC' - INTERVAL '30 MINUTES', '222222',
        current_timestamp at time zone 'UTC' - INTERVAL '30 MINUTES');
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id, pin_code,
                          pin_date_created, qr_code, qr_date_created)
VALUES (39, 'Trash', 'YELLOW', 2, false, 1, '333333', current_timestamp at time zone 'UTC' - INTERVAL '60 MINUTES',
        '333333', current_timestamp at time zone 'UTC' - INTERVAL '62 MINUTES');
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id, pin_code,
                          pin_date_created, qr_code, qr_date_created)
VALUES (40, 'Buy Soap', 'ORANGE', 1, true, 1, '444444', current_timestamp at time zone 'UTC' - INTERVAL '2 HOURS',
        '444444', current_timestamp at time zone 'UTC' - INTERVAL '1 DAY');
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id, pin_code,
                          pin_date_created, qr_code, qr_date_created)
VALUES (44, 'Bring Water', 'BLUE', 1, false, 1, '555555', current_timestamp at time zone 'UTC' - INTERVAL '1 DAY',
        '555555', current_timestamp at time zone 'UTC' - INTERVAL '2 DAY');
INSERT INTO public.queue (queue_id, name, color, creator_id, track_expenses, current_user_id, pin_code,
                          pin_date_created, qr_code, qr_date_created)
VALUES (46, 'Buy Sponge', 'PURPLE', 1, true, 1, '666666', current_timestamp at time zone 'UTC' - INTERVAL '7 DAY',
        '666666', current_timestamp at time zone 'UTC' - INTERVAL '7 DAY');
