-- ============================================================
-- BI 报表/数据大屏模块数据库初始化脚本
-- ============================================================

-- 仪表盘表
CREATE TABLE IF NOT EXISTS `rpt_dashboard` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(200) NOT NULL COMMENT '仪表盘名称',
    `description` text COMMENT '描述',
    `layout` text COMMENT '布局配置JSON',
    `status` tinyint DEFAULT '0' COMMENT '状态：0-草稿 1-发布',
    `cover_image` varchar(500) DEFAULT NULL COMMENT '封面图片',
    `is_public` tinyint(1) DEFAULT '0' COMMENT '是否公开分享',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仪表盘';

-- 图表配置表
CREATE TABLE IF NOT EXISTS `rpt_chart_config` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `dashboard_id` bigint DEFAULT NULL COMMENT '所属仪表盘ID',
    `title` varchar(200) DEFAULT NULL COMMENT '图表标题',
    `chart_type` varchar(20) DEFAULT NULL COMMENT '图表类型：line/bar/pie/table/number',
    `data_source_id` bigint DEFAULT NULL COMMENT '数据源ID',
    `query_sql` text COMMENT '查询SQL',
    `x_field` varchar(100) DEFAULT NULL COMMENT 'X轴字段',
    `y_fields` varchar(500) DEFAULT NULL COMMENT 'Y轴字段，逗号分隔',
    `style_options` text COMMENT '样式配置JSON',
    `sort_order` int DEFAULT '0' COMMENT '排序',
    `cache_seconds` int DEFAULT '0' COMMENT '缓存秒数',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_dashboard_id` (`dashboard_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图表配置';

-- 数据源配置表
CREATE TABLE IF NOT EXISTS `rpt_data_source` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `name` varchar(200) NOT NULL COMMENT '数据源名称',
    `db_type` varchar(20) DEFAULT 'mysql' COMMENT '数据库类型',
    `url` varchar(500) NOT NULL COMMENT '连接URL',
    `username` varchar(100) NOT NULL COMMENT '用户名',
    `password` varchar(200) DEFAULT NULL COMMENT '密码',
    `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用 1-启用',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源配置';
