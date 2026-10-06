package com.shengyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shengyi.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
/**
 * 报销人表
 *
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
