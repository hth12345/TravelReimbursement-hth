package com.shengyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shengyi.entity.Employee;
import com.shengyi.vo.EmployeeVO;
/**
 * 报销人下拉框
 */
import java.util.List;

public interface EmployeeService extends IService<Employee> {

    List<EmployeeVO> getEmployeeList(String keyword);
}
