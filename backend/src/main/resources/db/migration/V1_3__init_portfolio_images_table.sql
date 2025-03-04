drop table if exists portfolio_images;

create table portfolio_images(
    id int primary key auto_increment,
    filename varchar(255) not null,
    image blob not null,
    company_id int,

    foreign key (company_id) references companies(id) on delete cascade
);