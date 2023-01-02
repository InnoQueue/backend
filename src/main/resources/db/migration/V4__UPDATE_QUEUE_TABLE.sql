ALTER TABLE queue
    ADD COLUMN IF NOT EXISTS pin_code         varchar(8),
    ADD COLUMN IF NOT EXISTS qr_code          varchar(64),
    ADD COLUMN IF NOT EXISTS pin_date_created timestamp,
    ADD COLUMN IF NOT EXISTS qr_date_created  timestamp;

DROP TABLE queue_pin_code;
DROP TABLE queue_qr_code;
