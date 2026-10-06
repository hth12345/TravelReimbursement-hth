package com.shengyi.config;

/**
 * 操作类型枚举 — 对应异动日志的 operation_type 字段
 */
public enum OperationType {

    // ---- 报销单 ----
    REIMB_CREATE("报销单新增"),
    REIMB_UPDATE("报销单编辑"),
    REIMB_SUBMIT("报销单提交"),
    REIMB_WITHDRAW("报销单撤回"),
    REIMB_INVALIDATE("报销单作废"),
    REIMB_DELETE("报销单删除"),

    // ---- 行程 ----
    TRIP_CREATE("行程新增"),
    TRIP_UPDATE("行程编辑"),
    TRIP_DELETE("行程删除"),
    TRIP_COPY("行程复制"),

    // ---- 补助 ----
    SUBSIDY_SAVE("补助修改");

    private final String label;

    OperationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
