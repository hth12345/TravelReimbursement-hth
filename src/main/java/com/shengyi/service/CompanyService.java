package com.shengyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shengyi.entity.Company;
import com.shengyi.entity.Employee;
import com.shengyi.vo.CompanyVO;
import com.shengyi.vo.EmployeeVO;

import java.util.List;
/**
 * 费用归属公司下拉框
 */
public interface CompanyService extends IService<Company> {

    List<CompanyVO> getCompanyList();
}
