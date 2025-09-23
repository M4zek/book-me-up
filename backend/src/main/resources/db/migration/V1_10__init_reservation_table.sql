drop table if exists reservations;

create table reservations(
    id int primary key auto_increment,
    reservation_date datetime not null,
    reservation_number varchar(64) not null unique,
    status varchar(20) not null default 'PENDING',
        check (status in ('PENDING', 'ACCEPTED', 'REJECTED', 'COMPLETED', 'CANCELLED')),
    user_id int not null,
    company_offer_id bigint not null,

    foreign key (user_id) references users(id) on delete cascade,
    foreign key (company_offer_id) references company_offers(id) on delete cascade
)