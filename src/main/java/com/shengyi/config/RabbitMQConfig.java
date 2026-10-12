package com.shengyi.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置 — 声明审计日志交换机、队列、绑定关系
 */
@Configuration
public class RabbitMQConfig {

    /** 审计日志交换机 */
    public static final String AUDIT_EXCHANGE = "reimb.audit.exchange";

    /** 审计日志队列 */
    public static final String AUDIT_QUEUE = "reimb.audit.queue";

    /** 审计日志路由键 */
    public static final String AUDIT_ROUTING_KEY = "audit.log";

    @Bean
    public DirectExchange auditExchange() {
        return new DirectExchange(AUDIT_EXCHANGE, true, false);
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable(AUDIT_QUEUE).build();
    }

    @Bean
    public Binding auditBinding(Queue auditQueue, DirectExchange auditExchange) {
        return BindingBuilder.bind(auditQueue).to(auditExchange).with(AUDIT_ROUTING_KEY);
    }
}
