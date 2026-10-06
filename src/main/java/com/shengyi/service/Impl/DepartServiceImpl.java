package com.shengyi.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shengyi.entity.Depart;
import com.shengyi.mapper.DepartMapper;
import com.shengyi.service.DepartService;
import com.shengyi.vo.DepartVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartServiceImpl extends ServiceImpl<DepartMapper, Depart> implements DepartService {

    @Override
    @Cacheable(value = "depart")
    public List<DepartVO> GET_DEPARTMENT_LIST() {
        List<Depart> list = this.list();
        return list.stream().map(depart -> {
            DepartVO vo = new DepartVO();
            BeanUtils.copyProperties(depart, vo);
            return vo;
        }).toList();
    }
}
