package com.m4zek.backend.adapter;

import com.m4zek.backend.model.SystemAuditLog;
import com.m4zek.backend.repository.SysAuditLogRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSysAuditLogRepository extends SysAuditLogRepository, JpaRepository<SystemAuditLog, Integer> {
}
