-- =============================================
-- 差旅报销系统 - 数据库初始化脚本
-- =============================================

CREATE DATABASE IF NOT EXISTS travelreimbursement DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE travelreimbursement;

-- 1. 费用归属公司表
CREATE TABLE IF NOT EXISTS company (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reim_company_id VARCHAR(64) NOT NULL COMMENT '费用归属公司ID',
    reim_company_no VARCHAR(64) COMMENT '费用归属公司编号',
    reim_company_name VARCHAR(128) COMMENT '费用归属公司名称'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用归属公司表';

-- 2. 报销部门表
CREATE TABLE IF NOT EXISTS depart (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reim_department_id VARCHAR(64) NOT NULL COMMENT '报销部门ID',
    reim_department_no VARCHAR(64) COMMENT '报销部门编号',
    reim_department_name VARCHAR(128) COMMENT '报销部门名称'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报销部门表';

-- 3. 报销人表
CREATE TABLE IF NOT EXISTS employee (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reimburser_id VARCHAR(64) NOT NULL COMMENT '报销人员ID',
    reimburser_no VARCHAR(64) COMMENT '报销人员工号',
    reimburser_name VARCHAR(64) COMMENT '报销人员姓名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报销人表';

-- 4. 业务类型表
CREATE TABLE IF NOT EXISTS business_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_type_id VARCHAR(64) NOT NULL COMMENT '业务类型ID',
    business_type_no VARCHAR(64) COMMENT '业务类型编号',
    business_type_name VARCHAR(128) COMMENT '业务类型名称',
    there_subordinate_node VARCHAR(8) DEFAULT '0' COMMENT '是否有下级节点：0-无，1-有',
    superior_id VARCHAR(64) DEFAULT 'none' COMMENT '上级业务类型ID，none表示最上级',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted INT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务类型表';

-- 5. 城市类型配置表
CREATE TABLE IF NOT EXISTS city_type_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    city_no VARCHAR(32) COMMENT '城市编号',
    city_name VARCHAR(64) COMMENT '城市名称',
    city_type INT COMMENT '城市类型：1-一线，2-二线，3-三线',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城市类型配置表';

-- 6. 报销单主表
CREATE TABLE IF NOT EXISTS reimbursement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reimbursement_no VARCHAR(32) NOT NULL COMMENT '报销单号（全局唯一）',
    title VARCHAR(512) COMMENT '报销标题',
    reason TEXT COMMENT '报销事由',
    status INT DEFAULT 0 COMMENT '状态：0-草稿，1-审批中，2-审批通过，3-已完成，4-已作废',
    version INT DEFAULT 0 COMMENT '乐观锁版本号',
    reimburser_id VARCHAR(64) COMMENT '报销人ID',
    reimburser_no VARCHAR(64) COMMENT '报销人工号',
    reimburser_name VARCHAR(64) COMMENT '报销人姓名',
    department_id VARCHAR(64) COMMENT '报销部门ID',
    department_name VARCHAR(128) COMMENT '报销部门名称',
    department_no VARCHAR(64) COMMENT '报销部门编号',
    company_id VARCHAR(64) COMMENT '费用归属公司ID',
    company_name VARCHAR(128) COMMENT '费用归属公司名称',
    company_no VARCHAR(64) COMMENT '费用归属公司编号',
    business_type_id VARCHAR(64) COMMENT '业务类型ID',
    business_type_name VARCHAR(128) COMMENT '业务类型名称',
    subsidy_amount DECIMAL(12,2) DEFAULT 0.00 COMMENT '补助总金额',
    remarks VARCHAR(1024) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    UNIQUE KEY uk_reimbursement_no (reimbursement_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报销单主表';

-- 7. 补录行程表
CREATE TABLE IF NOT EXISTS trip (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reimbursement_id BIGINT NOT NULL COMMENT '报销单ID',
    traveler_id VARCHAR(64) COMMENT '出行人ID',
    traveler_name VARCHAR(64) COMMENT '出行人姓名',
    departure_city VARCHAR(64) COMMENT '出发城市',
    arrival_city VARCHAR(64) COMMENT '到达城市',
    start_date DATE COMMENT '出发日期',
    end_date DATE COMMENT '到达日期',
    description TEXT COMMENT '行程说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_reimbursement_id (reimbursement_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补录行程表';

-- 8. 补助日历表
CREATE TABLE IF NOT EXISTS subsidy_calendar (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reimbursement_id BIGINT NOT NULL COMMENT '报销单ID',
    trip_id BIGINT NOT NULL COMMENT '行程ID',
    calendar_date DATE NOT NULL COMMENT '具体补助日期',
    meal_standard DECIMAL(10,2) DEFAULT 0.00 COMMENT '餐补标准',
    meal_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '实际餐补金额',
    meal_checked INT DEFAULT 0 COMMENT '餐补是否勾选：0-否，1-是',
    traffic_standard DECIMAL(10,2) DEFAULT 0.00 COMMENT '交通补助标准',
    traffic_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '实际交通补助金额',
    traffic_checked INT DEFAULT 0 COMMENT '交通补助是否勾选：0-否，1-是',
    comm_standard DECIMAL(10,2) DEFAULT 0.00 COMMENT '通讯补助标准',
    comm_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '实际通讯补助金额',
    comm_checked INT DEFAULT 0 COMMENT '通讯补助是否勾选：0-否，1-是',
    deleted INT DEFAULT 0 COMMENT '逻辑删除：0-正常，1-已删除',
    INDEX idx_reimbursement_id (reimbursement_id),
    INDEX idx_trip_id (trip_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补助日历表';

-- 9. 审计日志表
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator_id VARCHAR(64) COMMENT '操作人ID',
    operator_name VARCHAR(64) COMMENT '操作人姓名',
    operation_time DATETIME COMMENT '操作时间',
    operation_type VARCHAR(64) COMMENT '操作类型',
    target_id VARCHAR(64) COMMENT '操作对象ID',
    pre_data TEXT COMMENT '操作前数据（JSON）',
    post_data TEXT COMMENT '操作后数据（JSON）',
    ip_address VARCHAR(64) COMMENT '客户端IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

-- 10. 补助金额修复任务表（本地消息表）
CREATE TABLE IF NOT EXISTS subsidy_fix_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reimbursement_id BIGINT NOT NULL COMMENT '待修复报销单ID',
    status INT DEFAULT 0 COMMENT '状态：0-待处理，1-已完成，2-失败',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补助金额修复任务表';

-- =============================================
-- 初始化测试数据
-- =============================================

-- 测试公司
INSERT INTO company (reim_company_id, reim_company_no, reim_company_name) VALUES
('C001', 'COMP-001', '盛意科技有限公司'),
('C002', 'COMP-002', '盛意信息技术有限公司');

-- 测试部门
INSERT INTO depart (reim_department_id, reim_department_no, reim_department_name) VALUES
('D001', 'DEPT-001', '技术部'),
('D002', 'DEPT-002', '市场部'),
('D003', 'DEPT-003', '财务部');

-- 测试员工
INSERT INTO employee (reimburser_id, reimburser_no, reimburser_name) VALUES
('E001', 'EMP-001', '张三'),
('E002', 'EMP-002', '李四'),
('E003', 'EMP-003', '王五');

-- 测试业务类型（树形结构）
INSERT INTO business_type (business_type_id, business_type_no, business_type_name, there_subordinate_node, superior_id) VALUES
('BT001', 'BT-001', '员工差旅活动', '1', 'none'),
('BT002', 'BT-002', '境内出差', '0', 'BT001'),
('BT003', 'BT-003', '境外出差', '0', 'BT001');

-- 测试城市类型配置
INSERT INTO city_type_config (city_no, city_name, city_type) VALUES
('001', '北京', 1),
('002', '上海', 1),
('003', '广州', 1),
('004', '深圳', 1),
('005', '成都', 2),
('006', '武汉', 2);
