ALTER TABLE companies DROP COLUMN IF EXISTS average_rating;
ALTER TABLE companies ADD COLUMN average_rating DOUBLE;

UPDATE companies c
    LEFT JOIN (
        SELECT o.company_id, AVG(r.rating) AS avg_rating
        FROM company_offers o
                 JOIN reviews r ON r.company_offer_id = o.id
        GROUP BY o.company_id
    ) sub ON c.id = sub.company_id
SET c.average_rating = sub.avg_rating;

UPDATE companies
SET average_rating = 0
WHERE average_rating IS NULL;

ALTER TABLE companies
    MODIFY average_rating DOUBLE NOT NULL DEFAULT 0;

CREATE INDEX idx_companies_avg_rating ON companies(average_rating);

# Full text for 3 tables:
ALTER TABLE addresses ADD FULLTEXT(city);
ALTER TABLE categories ADD FULLTEXT(name);
ALTER TABLE companies ADD FULLTEXT(name);