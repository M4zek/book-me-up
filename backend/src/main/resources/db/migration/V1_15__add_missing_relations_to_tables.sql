ALTER TABLE addresses
    ADD COLUMN company_id INT NOT NULL,
    ADD CONSTRAINT fk_addresses_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE;