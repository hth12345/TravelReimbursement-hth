package com.shengyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shengyi.dto.TripCopyDTO;
import com.shengyi.dto.TripSaveDTO;
import com.shengyi.entity.Trip;
import com.shengyi.vo.TripVO;

public interface TripService extends IService<Trip> {

    String POST_TRIP_SAVE(TripSaveDTO dto);

    void DELETE_TRIP(String tripId);

    TripVO POST_TRIP_COPY(TripCopyDTO dto);
}
