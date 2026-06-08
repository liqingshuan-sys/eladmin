package me.zhengjie.report.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 图表配置 — 定义单个图表的数据源和展示方式。
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "rpt_chart_config")
public class ChartConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "dashboard_id")
    @ApiModelProperty(value = "所属仪表盘ID")
    private Long dashboardId;

    @ApiModelProperty(value = "图表标题")
    private String title;

    @ApiModelProperty(value = "图表类型：line-折线 bar-柱状 pie-饼图 table-表格 number-数字卡")
    private String chartType;

    @ApiModelProperty(value = "数据源ID")
    private Long dataSourceId;

    @ApiModelProperty(value = "查询SQL")
    @Column(columnDefinition = "TEXT")
    private String querySql;

    @ApiModelProperty(value = "X轴字段")
    private String xField;

    @ApiModelProperty(value = "Y轴字段（逗号分隔多系列）")
    private String yFields;

    @ApiModelProperty(value = "样式配置（JSON）")
    @Column(columnDefinition = "TEXT")
    private String styleOptions;

    @ApiModelProperty(value = "排序")
    private Integer sortOrder = 0;

    @ApiModelProperty(value = "缓存秒数（0=不缓存）")
    private Integer cacheSeconds = 0;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "update_time")
    private Timestamp updateTime;
}
