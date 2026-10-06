package com.shengyi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shengyi.config.AuditContext;
import com.shengyi.config.OperationType;
import com.shengyi.entity.AuditLog;
import com.shengyi.mapper.AuditLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 异动日志服务 — 异步记录，不阻塞主业务流程
 */
@Service
public class AuditLogService {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private AuditLogMapper auditLogMapper;

    /**
     * 异步记录异动日志
     *
     * @param type     操作类型
     * @param targetId 操作对象 ID
     * @param preData  操作前数据（可为 null）
     * @param postData 操作后数据（可为 null）
     */
    @Async
    public void record(OperationType type, String targetId, Object preData, Object postData) {
        AuditLog log = new AuditLog();
        log.setOperatorId(AuditContext.getOperatorId());
        log.setOperatorName(AuditContext.getOperatorName());
        log.setOperationTime(LocalDateTime.now());
        log.setOperationType(type.name());
        log.setTargetId(targetId);
        log.setPreData(toJson(preData));
        log.setPostData(toJson(postData));
        log.setIpAddress(AuditContext.getClientIp());
        auditLogMapper.insert(log);
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
