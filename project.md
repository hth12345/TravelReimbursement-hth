# 差旅报销系统 - 开发进度

## 已完成

### 1. 基础接口
- GET /api/employee/list — 姓名/工号模糊查询
- GET /api/department/list — 部门下拉框
- GET /api/company/list — 公司下拉框
- GET /api/businessType/tree — 业务类型树形结构

### 2. 报销单列表
- POST /api/reimbursement/list — 分页查询 + 多条件筛选
- GET /api/reimbursement/detail/{id} — 详情（三次独立查询：报销单 → 行程 → 补助汇总）

### 3. 保存草稿
- POST /api/reimbursement/saveDraft
- 新建：生成单号 BX+yyyyMMdd+随机，通过ID补全关联 name/no
- 更新：乐观锁 version 校验 + status=0 限制

### 4. 提交报销单
- POST /api/reimbursement/submit
- 五阶段校验：存在性 → 状态 → 乐观锁 → 必填完整性（50001）→ 行程重叠（50031）→ 补助超标（50021）
- 通过后 UPDATE status=1

### 5. 校验体系
- ReimbursementDTO：@NotBlank（title/companyId/departmentId/reimburserId/businessTypeId）
- spring-boot-starter-validation（Spring Boot 3.x 需显式引入）
- Controller：@Valid 激活校验
- Service 层去掉必填字段 if 判空

### 6. 状态码体系
- StatusCode 枚举：20000/50001/50021/50031/60001
- BusinessException：携带状态码
- GlobalExceptionHandler：@RestControllerAdvice
  - MethodArgumentNotValidException → 50001
  - BusinessException → 按携带码返回
  - Exception → 60001

### 7. 乐观锁
- UPDATE 条件：WHERE id=? AND version=? AND status=0
- 命中 0 行 → 抛版本冲突异常

## 待办
- [ ] 列表查询含子孙节点（businessType 树形筛选）
- [ ] 补录行程 CRUD
- [ ] 补助日历 CRUD
- [ ] 50021/50031 校验在行程/补助保存时的前端实时校验

## 关键文件
| 文件 | 职责 |
|---|---|
| exception/StatusCode.java | 状态码枚举 |
| exception/BusinessException.java | 业务异常 |
| exception/GlobalExceptionHandler.java | 全局异常处理 |
| dto/ReimbursementDTO.java | 入参 DTO + @NotBlank |
| service/Impl/ReimbursementServiceImpl.java | 核心业务逻辑 |
| mapper/ReimbursementMapper.java | 含注解 SQL 方法 |
| controller/ReimbursementController.java | 7 个接口 |
| vo/Result.java | 统一响应体 |
| pom.xml | 新增 spring-boot-starter-validation |
