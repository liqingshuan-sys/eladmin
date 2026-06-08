package me.zhengjie.report.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 数据源配置 — 连接外部数据库获取报表数据。
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "rpt_data_source")
public class DataSourceConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID")
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "数据源名称")
    private String name;

    @ApiModelProperty(value = "数据库类型：mysql postgresql oracle sqlserver")
    private String dbType = "mysql";

    @NotBlank
    @ApiModelProperty(value = "连接URL")
    private String url;

    @NotBlank
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码（加密存储）")
    private String password;

    @ApiModelProperty(value = "状态：0-禁用 1-启用")
    private Integer status = 1;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    private Timestamp createTime;
}
