INSERT INTO users_data
    (first_name, last_name, date_of_birth, phone_number)
VALUES
    ('John',
     'Doe',
     '1990-05-14 00:00:00',
     '+48 600 123 456');


INSERT INTO users
    (address_email, password, user_data_id, is_block, is_enable)
VALUES
    ('john.doe@gmail.com',
     '$2a$10$PVkQ4ffxidufAlppUz8nAOXje8.OkgUSgStuLL/HRy2jnb41ZnLum',
     1,
     true,
     true);


INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1),
       (1,2);