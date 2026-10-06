package com.shengyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shengyi.dto.TripCopyDTO;
import com.shengyi.dto.TripSaveDTO;
import com.shengyi.entity.Trip;
import com.shengyi.vo.TripVO;

public interface TripService extends IService<Trip> {

    String saveTrip(TripSaveDTO dto);

    void deleteTrip(String tripId);

    TripVO copyTrip(TripCopyDTO dto);
}
