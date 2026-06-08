package me.zhengjie.workflow.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 流程实例 — 记录每个启动的流程运行实例。
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "wf_process_instance")
public class ProcessInstance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID")
    private Long id;

    @Column(name = "flowable_proc_inst_id", unique = true)
    @ApiModelProperty(value = "Flowable 流程实例 ID")
    private String flowableProcInstId;

    @Column(name = "proc_def_id")
    @ApiModelProperty(value = "关联流程定义 ID")
    private Long procDefId;

    @ApiModelProperty(value = "流程名称")
    private String processName;

    @ApiModelProperty(value = "业务单号")
    private String businessKey;

    @ApiModelProperty(value = "发起人")
    private String starter;

    @ApiModelProperty(value = "状态：0-运行 1-完成 2-挂起 3-终止")
    private Integer status = 0;

    @ApiModelProperty(value = "发起人部门")
    private String deptName;

    @Column(name = "start_time")
    @ApiModelProperty(value = "开始时间")
    private Timestamp startTime;

    @Column(name = "end_time")
    @ApiModelProperty(value = "结束时间")
    private Timestamp endTime;
}
