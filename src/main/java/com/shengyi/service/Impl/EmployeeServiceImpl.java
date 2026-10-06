package com.shengyi.service.Impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shengyi.entity.Employee;
import com.shengyi.mapper.EmployeeMapper;
import com.shengyi.service.EmployeeService;
import com.shengyi.vo.EmployeeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Override
    @Cacheable(value = "employee", key = "#keyword ?: 'all'")
    public List<EmployeeVO> GET_EMPLOYEE_LIST(String keyword) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Employee::getReimburserName, keyword)
                    .or()
                    .like(Employee::getReimburserNo, keyword)
            );
        }
        List<Employee> list = this.list(queryWrapper);
        return list.stream().map(employee -> {
            EmployeeVO vo = new EmployeeVO();
            BeanUtils.copyProperties(employee, vo);
            return vo;
        }).toList();
    }
}
