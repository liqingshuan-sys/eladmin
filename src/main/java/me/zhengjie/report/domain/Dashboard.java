package me.zhengjie.report.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 仪表盘 — 包含多个图表的可视化面板。
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "rpt_dashboard")
public class Dashboard implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID")
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "仪表盘名称")
    private String name;

    @ApiModelProperty(value = "描述")
    @Column(columnDefinition = "TEXT")
    private String description;

    @ApiModelProperty(value = "布局配置（JSON）")
    @Column(columnDefinition = "TEXT")
    private String layout;

    @ApiModelProperty(value = "是否启用：0-草稿 1-发布")
    private Integer status = 0;

    @ApiModelProperty(value = "封面图片URL")
    private String coverImage;

    @ApiModelProperty(value = "是否公开分享")
    private Boolean isPublic = false;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Timestamp updateTime;
}
