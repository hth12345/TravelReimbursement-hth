package com.shengyi.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shengyi.entity.CityTypeConfig;
import com.shengyi.mapper.CityTypeConfigMapper;
import com.shengyi.service.CityService;
import com.shengyi.vo.CityVO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl extends ServiceImpl<CityTypeConfigMapper, CityTypeConfig> implements CityService {

    @Override
    @Cacheable(value = "city")
    public List<CityVO> GET_CITY_LIST() {
        return this.list().stream().map(c -> {
            CityVO vo = new CityVO();
            vo.setCityNo(c.getCityNo());
            vo.setCityName(c.getCityName());
            vo.setCityType(c.getCityType());
            return vo;
        }).toList();
    }
}
