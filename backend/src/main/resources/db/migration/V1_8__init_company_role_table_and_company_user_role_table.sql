drop table if exists company_roles;

create table company_roles(
    id int auto_increment primary key,
    name varchar(255) not null
);

INSERT INTO company_roles (name) value ('COMPANY_EMPLOYEE');
INSERT INTO company_roles (name) value ('COMPANY_MANAGER');
INSERT INTO company_roles (name) value ('COMPANY_OWNER');

CREATE TABLE company_user_roles (
    id int auto_increment primary key,
    user_id int not null,
    company_id int not null,
    company_role_id int not null,

    foreign key (user_id) references users(id) on delete cascade,
    foreign key (company_id) references companies(id) on delete cascade,
    foreign key (company_role_id) references company_roles(id)

);