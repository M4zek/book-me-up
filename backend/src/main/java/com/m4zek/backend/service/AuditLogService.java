package com.m4zek.backend.service;


import com.m4zek.backend.model.SystemAuditLog;
import com.m4zek.backend.repository.SysAuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final SysAuditLogRepository repository;

    public AuditLogService(SysAuditLogRepository repository) {
        this.repository = repository;
    }


    public SystemAuditLog save(SystemAuditLog log){
        return this.repository.save(log);
    }


}
