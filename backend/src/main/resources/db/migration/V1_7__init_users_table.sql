drop table if exists users;

create table users (
    id int primary key auto_increment,
    address_email varchar(255) not null unique,
    password varchar(255) not null,
    user_data_id int not null,

    foreign key (user_data_id) references users_data(id) on delete cascade
);