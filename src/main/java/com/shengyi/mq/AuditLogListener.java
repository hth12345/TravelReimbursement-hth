package com.shengyi.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import com.shengyi.config.RabbitMQConfig;
import com.shengyi.entity.AuditLog;
import com.shengyi.mapper.AuditLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 审计日志消息消费者 — 异步写入数据库
 */
@Slf4j
@Component
public class AuditLogListener {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private AuditLogMapper auditLogMapper;

    @RabbitListener(queues = RabbitMQConfig.AUDIT_QUEUE)
    public void onMessage(String json, Channel channel,
                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            AuditLog auditLog = objectMapper.readValue(json, AuditLog.class);
            auditLogMapper.insert(auditLog);

            // 手动 ACK
            channel.basicAck(deliveryTag, false);
            log.debug("审计日志写入成功: type={}, targetId={}",
                    auditLog.getOperationType(), auditLog.getTargetId());
        } catch (IOException e) {
            log.error("审计日志反序列化失败，消息将被拒绝: {}", json, e);
            try {
                // 反序列化失败不重试，直接拒绝并丢弃
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                log.error("拒绝消息失败", ex);
            }
        } catch (Exception e) {
            log.error("审计日志写入数据库失败，消息将重新入队", e);
            try {
                // 写入失败时重新入队，等待重试
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException ex) {
                log.error("重新入队失败", ex);
            }
        }
    }
}
