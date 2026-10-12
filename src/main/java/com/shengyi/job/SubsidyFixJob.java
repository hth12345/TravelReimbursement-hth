package com.shengyi.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shengyi.entity.Reimbursement;
import com.shengyi.entity.SubsidyFixTask;
import com.shengyi.mapper.ReimbursementMapper;
import com.shengyi.mapper.SubsidyFixTaskMapper;
import com.shengyi.service.ReimbursementService;
import com.shengyi.vo.SubsidySummaryVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 补助金额修复定时任务 — 每 5 分钟扫描本地消息表，批量修复补助金额不一致的报销单
 * <p>
 * 使用 Redisson 分布式锁防止多实例重复执行。
 */
@Slf4j
@Component
public class SubsidyFixJob {

    private static final String LOCK_KEY = "job:subsidy_fix";
    private static final int BATCH_SIZE = 50;

    @Autowired
    private SubsidyFixTaskMapper subsidyFixTaskMapper;
    @Autowired
    private ReimbursementService reimbursementService;
    @Autowired
    private ReimbursementMapper reimbursementMapper;
    @Autowired
    private RedissonClient redissonClient;

    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 60 * 1000)
    public void execute() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        try {
            boolean acquired = lock.tryLock(3, 300, TimeUnit.SECONDS);
            if (!acquired) {
                log.debug("补助金额修复任务未获取到分布式锁，跳过本次执行");
                return;
            }

            doFix();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("补助金额修复任务被中断");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void doFix() {
        List<SubsidyFixTask> tasks = subsidyFixTaskMapper.selectPendingTasks(BATCH_SIZE);
        if (tasks.isEmpty()) {
            return;
        }

        log.info("补助金额修复任务开始，待处理任务数: {}", tasks.size());

        for (SubsidyFixTask task : tasks) {
            try {
                fixSubsidyAmount(task);
                subsidyFixTaskMapper.markCompleted(task.getId());
            } catch (Exception e) {
                log.error("补助金额修复失败: taskId={}, reimbursementId={}",
                        task.getId(), task.getReimbursementId(), e);
                subsidyFixTaskMapper.markFailed(task.getId());
            }
        }

        log.info("补助金额修复任务完成");
    }

    private void fixSubsidyAmount(SubsidyFixTask task) {
        Long reimbursementId = task.getReimbursementId();

        // 重新计算补助总金额
        List<SubsidySummaryVO> summaryList = reimbursementMapper
                .selectSubsidySummaryByReimbursementId(reimbursementId);

        BigDecimal totalSubsidy = BigDecimal.ZERO.setScale(2);
        if (summaryList != null) {
            for (SubsidySummaryVO summary : summaryList) {
                if (summary.getSubsidyAmount() != null) {
                    totalSubsidy = totalSubsidy.add(summary.getSubsidyAmount());
                }
            }
        }

        // 更新报销单补助总金额
        Reimbursement reimbursement = reimbursementService.getById(reimbursementId);
        if (reimbursement != null
                && reimbursement.getSubsidyAmount().compareTo(totalSubsidy) != 0) {
            reimbursement.setSubsidyAmount(totalSubsidy);
            reimbursementMapper.updateById(reimbursement);
            log.info("补助金额已修复: reimbursementId={}, old={}, new={}",
                    reimbursementId, reimbursement.getSubsidyAmount(), totalSubsidy);
        }
    }
}
