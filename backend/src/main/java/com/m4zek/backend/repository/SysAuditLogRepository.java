package com.m4zek.backend.repository;

import com.m4zek.backend.model.SystemAuditLog;

public interface SysAuditLogRepository {

    SystemAuditLog save(SystemAuditLog log);

}
