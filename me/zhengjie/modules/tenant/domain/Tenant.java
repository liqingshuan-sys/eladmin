package me.zhengjie.modules.tenant.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 租户 — SAAS 多租户支持。
 * 每条租户对应一个隔离的组织/企业。
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "sys_tenant")
public class Tenant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID")
    private Long id;

    @NotBlank
    @Column(unique = true)
    @ApiModelProperty(value = "租户编码（唯一标识）")
    private String tenantCode;

    @NotBlank
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    @ApiModelProperty(value = "联系人")
    private String contactPerson;

    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    @ApiModelProperty(value = "联系邮箱")
    private String contactEmail;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "租户套餐：basic-基础 pro-专业 enterprise-企业")
    private String plan = "basic";

    @ApiModelProperty(value = "用户上限")
    private Integer maxUsers = 10;

    @ApiModelProperty(value = "存储上限(MB)")
    private Integer maxStorage = 1024;

    @ApiModelProperty(value = "过期时间（null=永久）")
    private Timestamp expireTime;

    @ApiModelProperty(value = "状态：0-禁用 1-启用")
    private Integer status = 1;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "update_time")
    private Timestamp updateTime;
}
