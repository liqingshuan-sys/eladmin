-- ============================================================
-- 工作流引擎模块数据库初始化脚本
-- ============================================================

-- 流程定义表
CREATE TABLE IF NOT EXISTS `wf_process_definition` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `flowable_deploy_id` varchar(64) DEFAULT NULL COMMENT 'Flowable 部署ID',
    `flowable_proc_def_id` varchar(64) DEFAULT NULL COMMENT 'Flowable 流程定义ID',
    `name` varchar(200) NOT NULL COMMENT '流程名称',
    `description` text COMMENT '流程描述',
    `process_key` varchar(100) NOT NULL COMMENT '流程Key',
    `version` int DEFAULT '1' COMMENT '版本号',
    `bpmn_file_name` varchar(255) DEFAULT NULL COMMENT 'BPMN文件名',
    `status` tinyint DEFAULT '0' COMMENT '状态：0-已部署 1-已挂起',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '部署时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_flowable_deploy_id` (`flowable_deploy_id`),
    UNIQUE KEY `uk_flowable_proc_def_id` (`flowable_proc_def_id`),
    KEY `idx_process_key` (`process_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程定义';

-- 流程实例表
CREATE TABLE IF NOT EXISTS `wf_process_instance` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `flowable_proc_inst_id` varchar(64) DEFAULT NULL COMMENT 'Flowable 流程实例ID',
    `proc_def_id` bigint DEFAULT NULL COMMENT '流程定义ID',
    `process_name` varchar(200) DEFAULT NULL COMMENT '流程名称',
    `business_key` varchar(100) DEFAULT NULL COMMENT '业务单号',
    `starter` varchar(50) DEFAULT NULL COMMENT '发起人',
    `status` tinyint DEFAULT '0' COMMENT '状态：0-运行 1-完成 2-挂起 3-终止',
    `dept_name` varchar(100) DEFAULT NULL COMMENT '发起人部门',
    `start_time` datetime DEFAULT NULL COMMENT '开始时间',
    `end_time` datetime DEFAULT NULL COMMENT '结束时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_flowable_proc_inst_id` (`flowable_proc_inst_id`),
    KEY `idx_proc_def_id` (`proc_def_id`),
    KEY `idx_starter` (`starter`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程实例';

-- 工作流任务表
CREATE TABLE IF NOT EXISTS `wf_task` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `flowable_task_id` varchar(64) DEFAULT NULL COMMENT 'Flowable 任务ID',
    `proc_inst_id` bigint DEFAULT NULL COMMENT '流程实例ID',
    `task_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
    `task_def_key` varchar(100) DEFAULT NULL COMMENT '任务定义Key',
    `assignee` varchar(50) DEFAULT NULL COMMENT '负责人',
    `candidates` varchar(500) DEFAULT NULL COMMENT '候选人',
    `status` tinyint DEFAULT '0' COMMENT '状态：0-待办 1-已完成 2-已驳回',
    `comment` text COMMENT '审批意见',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_flowable_task_id` (`flowable_task_id`),
    KEY `idx_assignee` (`assignee`),
    KEY `idx_proc_inst_id` (`proc_inst_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作流任务';
