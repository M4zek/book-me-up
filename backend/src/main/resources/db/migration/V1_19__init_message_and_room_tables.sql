drop table if exists message;
drop table if exists room_users;
drop table if exists room;


create table room (
    id bigint auto_increment primary key,
    name varchar(255),
    type varchar (50) not null,
    created_date timestamp not null default current_timestamp,
    modified_date timestamp null on update current_timestamp
);

create table room_users (
    id bigint auto_increment primary key,
    user_id int not null,
    room_id bigint not null,
    role varchar(50) not null default 'member',

    foreign key (user_id) references users(id) on delete cascade,
    foreign key (room_id) references room(id) on delete cascade
);

create table message (
    id int primary key auto_increment,
    content text not null,
    message_type varchar(50) not null,
    room_id bigint not null,
    user_id int not null,

    created_date timestamp not null default current_timestamp,
    modified_date timestamp null default current_timestamp on update current_timestamp,

    foreign key (room_id) references room(id),
    foreign key (user_id) references users(id)

);