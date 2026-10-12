package com.shengyi.service;

import com.shengyi.config.OperationType;
import com.shengyi.mq.AuditMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 异动日志服务 — 通过 RabbitMQ 异步投递，不阻塞主业务流程
 */
@Service
public class AuditLogService {

    @Autowired
    private AuditMessagePublisher auditMessagePublisher;

    /**
     * 异步记录异动日志（通过 MQ 投递）
     *
     * @param type     操作类型
     * @param targetId 操作对象 ID
     * @param preData  操作前数据（可为 null）
     * @param postData 操作后数据（可为 null）
     */
    public void record(OperationType type, String targetId, Object preData, Object postData) {
        auditMessagePublisher.publish(type, targetId, preData, postData);
    }
}
