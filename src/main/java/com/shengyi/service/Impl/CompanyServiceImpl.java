package com.shengyi.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shengyi.entity.Company;
import com.shengyi.mapper.CompanyMapper;
import com.shengyi.service.CompanyService;
import com.shengyi.vo.CompanyVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
    @Override
    @Cacheable(value = "company")
    public List<CompanyVO> getCompanyList() {
        List<Company> list = this.list();
        return list.stream().map(company -> {
            CompanyVO vo = new CompanyVO();
            BeanUtils.copyProperties(company, vo);
            return vo;
        }).toList();
    }
}
