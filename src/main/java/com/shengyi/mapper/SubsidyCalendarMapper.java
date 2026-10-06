package com.shengyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shengyi.entity.SubsidyCalendar;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SubsidyCalendarMapper extends BaseMapper<SubsidyCalendar> {

    /**
     * 删除指定行程的所有补助日历记录（编辑时先删后建）
     */
    @Delete("DELETE FROM subsidy_calendar WHERE trip_id = #{tripId}")
    int deleteByTripId(@Param("tripId") Long tripId);
}
