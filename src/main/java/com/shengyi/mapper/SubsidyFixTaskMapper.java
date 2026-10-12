package com.shengyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shengyi.entity.SubsidyFixTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SubsidyFixTaskMapper extends BaseMapper<SubsidyFixTask> {

    /**
     * 查询待处理的修复任务（限制批次大小）
     */
    @Select("SELECT * FROM subsidy_fix_task WHERE status = 0 ORDER BY create_time ASC LIMIT #{limit}")
    List<SubsidyFixTask> selectPendingTasks(@Param("limit") int limit);

    /**
     * 标记任务为已完成
     */
    @Update("UPDATE subsidy_fix_task SET status = 1, update_time = NOW() WHERE id = #{id}")
    int markCompleted(@Param("id") Long id);

    /**
     * 标记任务为失败并增加重试次数
     */
    @Update("UPDATE subsidy_fix_task SET status = 2, retry_count = retry_count + 1, update_time = NOW() WHERE id = #{id}")
    int markFailed(@Param("id") Long id);

    /**
     * 重置失败任务为待处理（重试）
     */
    @Update("UPDATE subsidy_fix_task SET status = 0, update_time = NOW() WHERE id = #{id} AND retry_count < 3")
    int resetForRetry(@Param("id") Long id);
}
