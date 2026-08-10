DROP TABLE IF EXISTS system_audit_logs;

CREATE TABLE system_audit_logs (
    id int AUTO_INCREMENT PRIMARY KEY,
    actor_id int NULL,
    action VARCHAR(255) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id VARCHAR(100) NOT NULL,
    details TEXT NULL,
    ip_address VARCHAR(45) NULL,
    timestamp DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT fk_audit_logs_actor
        FOREIGN KEY (actor_id)
            REFERENCES users (id)
            ON DELETE SET NULL
);

CREATE INDEX idx_audit_resource ON system_audit_logs (resource_type, resource_id);
CREATE INDEX idx_audit_actor ON system_audit_logs (actor_id);
CREATE INDEX idx_audit_timestamp ON system_audit_logs (timestamp);