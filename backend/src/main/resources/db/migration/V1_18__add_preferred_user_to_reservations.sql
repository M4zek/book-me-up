ALTER TABLE reservations
    ADD COLUMN preferred_user_id INT;

ALTER TABLE reservations
    ADD CONSTRAINT fk_reservation_preferred_user
        FOREIGN KEY (preferred_user_id)
            REFERENCES users(id)
            ON DELETE SET NULL;