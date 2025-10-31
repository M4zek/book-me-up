-- Modify companies (name, description) from varchar to text.
ALTER TABLE companies
    MODIFY COLUMN name TEXT NOT NULL,
    MODIFY COLUMN description TEXT NOT NULL;


-- Modify company_offers (name, description) from varchar to text.
ALTER TABLE company_offers
    MODIFY COLUMN name TEXT NOT NULL,
    MODIFY COLUMN description TEXT NOT NULL;

-- Modify reviews (comment) from varchar to text.
ALTER TABLE reviews
    MODIFY COLUMN comment TEXT NOT NULL;