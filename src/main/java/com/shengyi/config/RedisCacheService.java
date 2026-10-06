package com.shengyi.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shengyi.dto.ReimbursementListDTO;
import com.shengyi.vo.PageResult;
import com.shengyi.vo.ReimbursementListVO;
import com.shengyi.vo.SubsidySummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * Redis 缓存服务 — 封装哈希结构写/读与热点数据 TTL 管理
 *
 * 设计原则：
 * - 报销单列表使用 Redis Hash 结构存储，避免大 key，单 field ≤ 10KB
 * - 补助汇总使用简单 String 存储（数据量小）
 * - 热点数据统一 30 分钟 TTL
 */
@Service
public class RedisCacheService {

    private static final String REIMB_LIST_HASH_KEY = "reimb:list:cache";
    private static final String SUBSIDY_SUMMARY_PREFIX = "subsidy:summary:";
    private static final Duration HOT_DATA_TTL = Duration.ofMinutes(30);

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // ==================== 报销单列表缓存（Hash 结构） ====================

    /**
     * 从 Hash 中读取缓存的报销单列表分页结果
     * @param queryHash 查询条件的 MD5 哈希
     * @return 缓存的分页结果，未命中返回 null
     */
    public PageResult<ReimbursementListVO> getReimbList(String queryHash) {
        String json = (String) stringRedisTemplate.opsForHash().get(REIMB_LIST_HASH_KEY, queryHash);
        if (json == null) {
            return null;
        }
        try {
            JavaType type = objectMapper.getTypeFactory()
                    .constructParametricType(PageResult.class, ReimbursementListVO.class);
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            // 反序列化异常时删除脏数据，视为 miss
            stringRedisTemplate.opsForHash().delete(REIMB_LIST_HASH_KEY, queryHash);
            return null;
        }
    }

    /**
     * 将报销单列表分页结果写入 Hash 缓存
     * @param queryHash 查询条件的 MD5 哈希
     * @param result 分页结果
     */
    public void setReimbList(String queryHash, PageResult<ReimbursementListVO> result) {
        try {
            String json = objectMapper.writeValueAsString(result);
            stringRedisTemplate.opsForHash().put(REIMB_LIST_HASH_KEY, queryHash, json);
            // 每次写入时刷新整个 Hash 的 TTL
            stringRedisTemplate.expire(REIMB_LIST_HASH_KEY, HOT_DATA_TTL);
        } catch (Exception e) {
            // 序列化失败不阻塞业务
        }
    }

    /**
     * 清除全部报销单列表缓存（任何报销单变更时调用）
     */
    public void evictReimbListCache() {
        stringRedisTemplate.delete(REIMB_LIST_HASH_KEY);
    }

    // ==================== 补助汇总缓存（String 结构） ====================

    /**
     * 读取补助汇总缓存
     * @param reimbursementId 报销单 ID
     * @return 缓存列表，未命中返回 null
     */
    public List<SubsidySummaryVO> getSubsidySummary(String reimbursementId) {
        String key = SUBSIDY_SUMMARY_PREFIX + reimbursementId;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<SubsidySummaryVO>>() {});
        } catch (Exception e) {
            stringRedisTemplate.delete(key);
            return null;
        }
    }

    /**
     * 写入补助汇总缓存
     * @param reimbursementId 报销单 ID
     * @param list 补助汇总列表
     */
    public void setSubsidySummary(String reimbursementId, List<SubsidySummaryVO> list) {
        try {
            String key = SUBSIDY_SUMMARY_PREFIX + reimbursementId;
            String json = objectMapper.writeValueAsString(list);
            stringRedisTemplate.opsForValue().set(key, json, HOT_DATA_TTL);
        } catch (Exception e) {
            // 序列化失败不阻塞业务
        }
    }

    /**
     * 清除指定报销单的补助汇总缓存
     * @param reimbursementId 报销单 ID
     */
    public void evictSubsidySummary(String reimbursementId) {
        stringRedisTemplate.delete(SUBSIDY_SUMMARY_PREFIX + reimbursementId);
    }

    // ==================== 工具方法 ====================

    /**
     * 根据查询条件 DTO 计算 MD5 哈希作为缓存 field
     */
    public static String computeQueryHash(ReimbursementListDTO dto) {
        String raw = String.join("|",
                nvl(dto.getReimbursementNo()),
                nvl(dto.getTitle()),
                nvl(dto.getReason()),
                nvl(dto.getCompanyId()),
                nvl(dto.getDepartmentId()),
                nvl(dto.getReimburserId()),
                nvl(dto.getBusinessTypeId()),
                String.valueOf(dto.getPageNo()),
                String.valueOf(dto.getPageSize())
        );
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    private static String nvl(String s) {
        return s == null ? "" : s;
    }
}
