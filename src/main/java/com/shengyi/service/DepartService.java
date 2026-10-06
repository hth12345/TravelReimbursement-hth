package com.shengyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shengyi.entity.Depart;
import com.shengyi.entity.Employee;
import com.shengyi.vo.DepartVO;
import com.shengyi.vo.EmployeeVO;

import java.util.List;

/**
 * 报销部门下拉框
 */
public interface DepartService extends IService<Depart> {

    List<DepartVO> getDepartmentList();
}
