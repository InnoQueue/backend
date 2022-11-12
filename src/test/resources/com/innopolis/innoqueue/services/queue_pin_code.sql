INSERT INTO queue_pin_code (queue_id, pin_code, id, date_created)
VALUES (6, '111111', 1, current_timestamp at time zone 'UTC');
INSERT INTO queue_pin_code (queue_id, pin_code, id, date_created)
VALUES (34, '222222', 2, current_timestamp at time zone 'UTC' - INTERVAL '30 MINUTES');
INSERT INTO queue_pin_code (queue_id, pin_code, id, date_created)
VALUES (39, '333333', 3, current_timestamp at time zone 'UTC' - INTERVAL '60 MINUTES');
INSERT INTO queue_pin_code (queue_id, pin_code, id, date_created)
VALUES (40, '444444', 4, current_timestamp at time zone 'UTC' - INTERVAL '2 HOURS');
INSERT INTO queue_pin_code (queue_id, pin_code, id, date_created)
VALUES (44, '555555', 5, current_timestamp at time zone 'UTC' - INTERVAL '1 DAY');
INSERT INTO queue_pin_code (queue_id, pin_code, id, date_created)
VALUES (46, '666666', 6, current_timestamp at time zone 'UTC' - INTERVAL '7 DAY');
