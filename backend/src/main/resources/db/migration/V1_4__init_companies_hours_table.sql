drop table if exists companies_hours;

create table companies_hours(
    id int primary key auto_increment,
    day_of_week varchar(25) not null,
    open_time varchar(10) not null,
    close_time varchar(10) not null,
    is_open boolean not null,
    company_id int,

    foreign key (company_id) references companies(id) on delete cascade
);