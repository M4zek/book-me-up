drop table if exists categories;

create table categories (
    id int primary key auto_increment,
    name varchar(256) not null
);
