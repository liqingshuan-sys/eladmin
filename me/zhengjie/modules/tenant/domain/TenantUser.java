package me.zhengjie.modules.tenant.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 租户用户关联 — 记录用户所属租户。
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "sys_tenant_user")
public class TenantUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id")
    @ApiModelProperty(value = "租户ID")
    private Long tenantId;

    @Column(name = "user_id")
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "角色：admin-管理员 user-普通成员")
    private String role = "user";

    @Column(name = "join_time")
    private Timestamp joinTime;
}
