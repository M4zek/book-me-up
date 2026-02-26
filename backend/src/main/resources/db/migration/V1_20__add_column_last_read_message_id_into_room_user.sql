alter table room_users
    add column last_read_message_id int null,
    add constraint fk_room_users_last_read_message
        foreign key (last_read_message_id) references message(id)
            on delete set null;