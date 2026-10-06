package com.shengyi.controller;

import com.shengyi.service.*;
import com.shengyi.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 基础数据控制器 — 下拉框数据源
 */
@RestController
@RequestMapping("/api")
public class BasicDataController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DepartService departService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private BusinessTypeService businessTypeService;
    @Autowired
    private CityService cityService;

    /**
     * 获取员工列表
     * @param keyword 搜索关键字，用于模糊匹配员工姓名或工号
     * @return 包含员工信息的列表结果
     */
    @GetMapping("/employee/list")
    public Result<List<EmployeeVO>> GET_EMPLOYEE_LIST(String keyword) {
        List<EmployeeVO> list = employeeService.GET_EMPLOYEE_LIST(keyword);
        return Result.success(list);
    }

    /**
     * 获取部门列表
     * @return 包含部门信息的列表结果
     */
    @GetMapping("/department/list")
    public Result<List<DepartVO>> GET_DEPARTMENT_LIST() {
        List<DepartVO> list = departService.GET_DEPARTMENT_LIST();
        return Result.success(list);
    }

    /**
     * 获取公司列表
     * @return 包含公司信息的列表结果
     */
    @GetMapping("/company/list")
    public Result<List<CompanyVO>> GET_COMPANY_LIST() {
        List<CompanyVO> list = companyService.GET_COMPANY_LIST();
        return Result.success(list);
    }

    /**
     * 获取业务类型树形结构
     * @return 包含业务类型树形节点的列表结果
     */
    @GetMapping("/businessType/tree")
    public Result<List<BusinessTypeTreeNode>> GET_BUSINESS_TYPE_TREE() {
        List<BusinessTypeTreeNode> list = businessTypeService.GET_BUSINESS_TYPE_TREE();
        return Result.success(list);
    }

    /**
     * 获取城市列表（出发/到达城市下拉框）
     */
    @GetMapping("/city/list")
    public Result<List<CityVO>> GET_CITY_LIST() {
        List<CityVO> list = cityService.GET_CITY_LIST();
        return Result.success(list);
    }
}
