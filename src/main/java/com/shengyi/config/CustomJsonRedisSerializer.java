package com.shengyi.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * 自定义 JSON Redis 序列化器 — 替代 GenericJackson2JsonRedisSerializer
 * <p>
 * 不使用 Jackson 的 DefaultTyping（@class 标记），
 * 而是用 JSON 数组包装：[全限定类名, JSON字符串]。
 * 避免了 @class 属性与泛型集合的兼容性问题。
 */
public class CustomJsonRedisSerializer implements RedisSerializer<Object> {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public byte[] serialize(Object obj) throws SerializationException {
        if (obj == null) {
            return new byte[0];
        }
        try {
            String className = obj.getClass().getName();
            String json = objectMapper.writeValueAsString(obj);
            // 格式：[类名, JSON字符串]
            String wrapped = objectMapper.writeValueAsString(new String[]{className, json});
            return wrapped.getBytes();
        } catch (JsonProcessingException e) {
            throw new SerializationException("Serialize failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            String[] pair = objectMapper.readValue(bytes, String[].class);
            Class<?> clazz = Class.forName(pair[0]);
            JavaType javaType = objectMapper.getTypeFactory().constructType(clazz);
            return objectMapper.readValue(pair[1], javaType);
        } catch (Exception e) {
            throw new SerializationException("Deserialize failed: " + e.getMessage(), e);
        }
    }
}
