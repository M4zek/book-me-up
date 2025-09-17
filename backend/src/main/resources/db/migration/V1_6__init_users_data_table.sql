drop table if exists users_data;

create table users_data(
    id int primary key auto_increment,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    date_of_birth datetime not null,
    phone_number varchar(25) not null,
    photo blob not null
);


