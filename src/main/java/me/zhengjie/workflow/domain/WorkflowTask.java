package me.zhengjie.workflow.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 工作流任务 — 记录待办/已办任务。
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "wf_task")
public class WorkflowTask implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "flowable_task_id", unique = true)
    @ApiModelProperty(value = "Flowable 任务 ID")
    private String flowableTaskId;

    @Column(name = "proc_inst_id")
    @ApiModelProperty(value = "流程实例 ID")
    private Long procInstId;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务定义 Key")
    private String taskDefKey;

    @ApiModelProperty(value = "负责人")
    private String assignee;

    @ApiModelProperty(value = "候选人（逗号分隔）")
    private String candidates;

    @ApiModelProperty(value = "状态：0-待办 1-已完成 2-已驳回")
    private Integer status = 0;

    @ApiModelProperty(value = "审批意见")
    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间")
    private Timestamp createTime;

    @Column(name = "complete_time")
    @ApiModelProperty(value = "完成时间")
    private Timestamp completeTime;
}
