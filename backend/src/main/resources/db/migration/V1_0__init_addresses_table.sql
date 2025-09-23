drop table if exists addresses;

create table addresses (
    id int primary key auto_increment,
    city varchar(100) not null,
    postal_code varchar(20) not null,
    street varchar(255) not null,
    building_number varchar(10) not null
);