# Create table for file metadata
drop table if exists stored_file;


create table stored_file(
    id bigint primary key auto_increment,
    object_key varchar(255) not null unique ,
    original_file_name varchar(255) not null,
    content_type varchar(255) not null,
    size bigint,
    type varchar(100) not null,

    created_date timestamp not null default current_timestamp,
    modified_date timestamp null on update current_timestamp
);
 # -----------------------------------------------

# Add relations
ALTER TABLE stored_file
    ADD COLUMN user_data_id INT NULL,
    ADD CONSTRAINT fk_file_user_data_id FOREIGN KEY (user_data_id)
        REFERENCES users_data(id) ON DELETE SET NULL,

    ADD COLUMN company_id INT NULL,
    ADD CONSTRAINT fk_file_company_id FOREIGN KEY (company_id)
        REFERENCES companies(id) ON DELETE SET NULL;


# Delete table (Portfolio images)
DROP TABLE IF EXISTS portfolio_images;

# Drop relation columns portfolio_images
# Drop column photo from user data table
ALTER TABLE users_data DROP COLUMN IF EXISTS photo;

# Delete column logo form company table
ALTER TABLE companies DROP COLUMN IF EXISTS logo;

