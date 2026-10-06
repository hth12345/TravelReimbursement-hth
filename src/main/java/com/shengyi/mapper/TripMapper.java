package com.shengyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shengyi.entity.Trip;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface TripMapper extends BaseMapper<Trip> {

    int COUNT_DATE_OVERLAP(@Param("reimbursementId") Long reimbursementId,
                           @Param("travelerId") String travelerId,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate);

    int COUNT_DATE_OVERLAP_EXCLUDE_SELF(@Param("reimbursementId") Long reimbursementId,
                                        @Param("travelerId") String travelerId,
                                        @Param("tripId") Long tripId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
}
