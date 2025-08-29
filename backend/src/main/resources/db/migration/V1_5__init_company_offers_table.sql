drop table if exists companies_services;

create table company_offers(
    id int primary key auto_increment,
    name varchar(255) not null,
    description varchar(2048) not null,
    price double not null,
    duration int not null,
    company_id int,

    foreign key (company_id) references companies(id) on delete cascade
)