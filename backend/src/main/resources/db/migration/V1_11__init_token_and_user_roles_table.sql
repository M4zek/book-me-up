drop table if exists roles;

create table roles(
    id int auto_increment primary key,
    name varchar(255) not null unique
);


INSERT INTO roles (name) value ('ROLE_ADMIN'), ('ROLE_USER');

drop table if exists user_roles;
create table user_roles(
    user_id int not null,
    role_id int not null,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);


drop table if exists refresh_token;
create table refresh_token(
    id int primary key auto_increment,
    expire_date datetime not null,
    refresh_token varchar(255) not null,
    user_id int not null,

    FOREIGN KEY (user_id) REFERENCES users(id)
)