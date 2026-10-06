package com.shengyi.vo;

import lombok.Data;

import java.util.List;

@Data
public class BusinessTypeTreeNode {
    private String businessTypeId;
    private String businessTypeNo;
    private String businessTypeName;
    private List<BusinessTypeTreeNode> children = new java.util.ArrayList<>();
}
