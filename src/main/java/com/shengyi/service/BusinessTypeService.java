package com.shengyi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shengyi.entity.BusinessType;
import com.shengyi.vo.BusinessTypeTreeNode;
import com.shengyi.vo.BusinessTypeVO;

import java.util.List;

/**
 * 业务类型下拉框
 */
public interface BusinessTypeService extends IService<BusinessType> {

    List<BusinessTypeTreeNode> GET_BUSINESS_TYPE_TREE();
}
