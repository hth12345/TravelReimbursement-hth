package com.shengyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shengyi.entity.CityTypeConfig;
import com.shengyi.vo.CityVO;

import java.util.List;

public interface CityService extends IService<CityTypeConfig> {
    List<CityVO> getCityList();
}
