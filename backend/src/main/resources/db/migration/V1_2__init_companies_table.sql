drop table if exists companies;

create table companies(
    id int primary key auto_increment,
    name varchar(255) not null,
    description varchar(1024) not null ,
    logo blob not null,
    category_id int not null,

    foreign key (category_id) references categories(id) on delete cascade
);
