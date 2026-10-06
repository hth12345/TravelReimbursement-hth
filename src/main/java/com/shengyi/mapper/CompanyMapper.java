package com.shengyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shengyi.entity.Company;
import com.shengyi.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
/**
 * 费用归属公司表
 *
 */
@Mapper
public interface CompanyMapper extends BaseMapper<Company> {

}
