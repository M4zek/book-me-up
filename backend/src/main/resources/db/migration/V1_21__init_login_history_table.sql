drop table if exists login_history;

create table login_history(
    id int primary key auto_increment,
    user_id int null,
    email varchar(100) not null,
    ip_address varchar(45) not null,
    user_agent varchar(512),
    success boolean not null,
    failure_reason varchar(255),

    created_date timestamp not null default current_timestamp,
    modified_date timestamp null default current_timestamp on update current_timestamp
);

create index idx_login_user_id
    on login_history(user_id);

create index idx_login_email
    on login_history(email);

create index idx_login_created
    on login_history(created_date);

create index idx_login_email_success
    on login_history(email, success);