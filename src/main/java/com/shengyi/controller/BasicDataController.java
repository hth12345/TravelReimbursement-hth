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
    public Result<List<EmployeeVO>> getEmployeeList(String keyword) {
        List<EmployeeVO> list = employeeService.getEmployeeList(keyword);
        return Result.success(list);
    }

    /**
     * 获取部门列表
     * @return 包含部门信息的列表结果
     */
    @GetMapping("/department/list")
    public Result<List<DepartVO>> getDepartmentList() {
        List<DepartVO> list = departService.getDepartmentList();
        return Result.success(list);
    }

    /**
     * 获取公司列表
     * @return 包含公司信息的列表结果
     */
    @GetMapping("/company/list")
    public Result<List<CompanyVO>> getCompanyList() {
        List<CompanyVO> list = companyService.getCompanyList();
        return Result.success(list);
    }

    /**
     * 获取业务类型树形结构
     * @return 包含业务类型树形节点的列表结果
     */
    @GetMapping("/businessType/tree")
    public Result<List<BusinessTypeTreeNode>> getBusinessTypeTree() {
        List<BusinessTypeTreeNode> list = businessTypeService.getBusinessTypeTree();
        return Result.success(list);
    }

    /**
     * 获取城市列表（出发/到达城市下拉框）
     */
    @GetMapping("/city/list")
    public Result<List<CityVO>> getCityList() {
        List<CityVO> list = cityService.getCityList();
        return Result.success(list);
    }
}
