drop table if exists reviews;

create table reviews(
    id int auto_increment primary key,
    comment varchar(1024) not null,
    rating tinyint not null check ( rating between 1 and 5),
    user_id int not null,
    company_offer_id bigint not null,

    foreign key (user_id) references users(id) on delete cascade,
    foreign key (company_offer_id) references company_offers(id) on delete cascade
)