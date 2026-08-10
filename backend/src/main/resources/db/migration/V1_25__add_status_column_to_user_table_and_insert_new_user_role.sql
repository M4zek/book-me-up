ALTER TABLE users
    ADD COLUMN status varchar(20) NOT NULL DEFAULT 'NOT_ACTIVE'
        CHECK (status IN ('ACTIVE', 'NOT_ACTIVE', 'BLOCK', 'SUSPENDED')),
    ADD COLUMN suspended_to DATETIME NULL;


-- Migration old data based on old columns (is_enable and is_block)
-- is_block has wrong name... Should be 'is_not_block'
-- 1 - Account no block , 0-Account block
UPDATE users
    SET status = CASE
         WHEN is_block = FALSE THEN 'BLOCKED'
         WHEN is_enable = TRUE THEN 'ACTIVE'
         ELSE 'NOT_ACTIVE'
    END;


-- AFTER MIGRATION TO STATUS DELETE COLUMNS
ALTER TABLE users DROP COLUMN is_block, DROP COLUMN is_enable;

-- ADD NEW ROLE TO REVIEW ADMIN DATA - NO UPDATE ONLY READ
INSERT INTO roles (name) value ('ROLE_ADMIN_VIEWER');