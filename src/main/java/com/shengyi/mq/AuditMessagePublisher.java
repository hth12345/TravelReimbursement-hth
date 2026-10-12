package com.shengyi.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shengyi.config.AuditContext;
import com.shengyi.config.OperationType;
import com.shengyi.config.RabbitMQConfig;
import com.shengyi.entity.AuditLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 审计日志消息生产者 — 将审计事件发送到 RabbitMQ，异步解耦
 */
@Slf4j
@Component
public class AuditMessagePublisher {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发布审计日志消息
     *
     * @param type     操作类型
     * @param targetId 操作对象 ID
     * @param preData  操作前数据（可为 null）
     * @param postData 操作后数据（可为 null）
     */
    @Async
    public void publish(OperationType type, String targetId, Object preData, Object postData) {
        AuditLog auditLog = new AuditLog();
        auditLog.setOperatorId(AuditContext.getOperatorId());
        auditLog.setOperatorName(AuditContext.getOperatorName());
        auditLog.setOperationTime(LocalDateTime.now());
        auditLog.setOperationType(type.name());
        auditLog.setTargetId(targetId);
        auditLog.setPreData(toJson(preData));
        auditLog.setPostData(toJson(postData));
        auditLog.setIpAddress(AuditContext.getClientIp());

        try {
            String json = objectMapper.writeValueAsString(auditLog);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.AUDIT_EXCHANGE,
                    RabbitMQConfig.AUDIT_ROUTING_KEY,
                    json
            );
            log.debug("审计日志消息已发送: type={}, targetId={}", type.name(), targetId);
        } catch (JsonProcessingException e) {
            log.error("审计日志序列化失败: type={}, targetId={}", type.name(), targetId, e);
        }
    }

    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
