ALTER TABLE companies
    MODIFY COLUMN logo mediumblob;

ALTER TABLE portfolio_images
    MODIFY COLUMN image mediumblob not null ;

ALTER TABLE users_data
    MODIFY COLUMN photo mediumblob;