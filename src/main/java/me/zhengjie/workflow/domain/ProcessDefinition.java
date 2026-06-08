package me.zhengjie.workflow.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 流程定义 — 记录已部署的 BPMN 流程模型。
 * 每次通过 Flowable 部署 BPMN 文件时同步记录到此表。
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "wf_process_definition")
public class ProcessDefinition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "flowable_deploy_id", unique = true)
    @ApiModelProperty(value = "Flowable 部署 ID")
    private String flowableDeployId;

    @Column(name = "flowable_proc_def_id", unique = true)
    @ApiModelProperty(value = "Flowable 流程定义 ID")
    private String flowableProcDefId;

    @NotBlank
    @ApiModelProperty(value = "流程名称")
    private String name;

    @ApiModelProperty(value = "流程描述")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @ApiModelProperty(value = "流程 Key（唯一标识）")
    private String processKey;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "BPMN 文件名")
    private String bpmnFileName;

    @ApiModelProperty(value = "状态：0-已部署 1-已挂起")
    private Integer status = 0;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @Column(name = "create_time")
    @ApiModelProperty(value = "部署时间")
    private Timestamp createTime;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Timestamp updateTime;
}
