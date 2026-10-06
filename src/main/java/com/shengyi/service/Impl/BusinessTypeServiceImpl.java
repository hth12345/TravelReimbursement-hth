package com.shengyi.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shengyi.entity.BusinessType;
import com.shengyi.mapper.BusinessTypeMapper;
import com.shengyi.service.BusinessTypeService;
import com.shengyi.vo.BusinessTypeTreeNode;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BusinessTypeServiceImpl extends ServiceImpl<BusinessTypeMapper, BusinessType> implements BusinessTypeService {
    @Override
    @Cacheable(value = "businessType")
    public List<BusinessTypeTreeNode> getBusinessTypeTree() {
        List<BusinessType> list = this.list();

        // 一条 SQL 查出全部，转成 Map（businessTypeId → superiorId）
        Map<String, String> superiorMap = list.stream()
                .collect(Collectors.toMap(BusinessType::getBusinessTypeId, BusinessType::getSuperiorId));

        // 实体 → TreeNode
        Map<String, BusinessTypeTreeNode> nodeMap = list.stream().map(b -> {
            BusinessTypeTreeNode node = new BusinessTypeTreeNode();
            node.setBusinessTypeId(b.getBusinessTypeId());
            node.setBusinessTypeNo(b.getBusinessTypeNo());
            node.setBusinessTypeName(b.getBusinessTypeName());
            return node;
        }).collect(Collectors.toMap(BusinessTypeTreeNode::getBusinessTypeId, node -> node));

        // 构建树
        List<BusinessTypeTreeNode> rootNodes = new ArrayList<>();
        for (BusinessTypeTreeNode node : nodeMap.values()) {
            String superiorId = superiorMap.get(node.getBusinessTypeId());
            if ("none".equals(superiorId) || superiorId == null || !nodeMap.containsKey(superiorId)) {
                rootNodes.add(node);
            } else {
                BusinessTypeTreeNode parent = nodeMap.get(superiorId);
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }

        return rootNodes;
    }
}
