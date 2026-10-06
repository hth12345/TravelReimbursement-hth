package com.shengyi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shengyi.entity.Depart;
import com.shengyi.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
/**
 * 报销部门表
 *
 */
@Mapper
public interface DepartMapper extends BaseMapper<Depart> {

}
