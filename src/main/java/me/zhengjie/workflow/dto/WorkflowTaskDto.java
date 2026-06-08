package me.zhengjie.workflow.dto;

import lombok.Data;
import java.sql.Timestamp;

/**
 * 工作流任务 DTO
 */
@Data
public class WorkflowTaskDto {
    private Long id;
    private String flowableTaskId;
    private Long procInstId;
    private String taskName;
    private String taskDefKey;
    private String assignee;
    private String candidates;
    private Integer status;
    private String comment;
    private String processName;
    private String businessKey;
    private String starter;
    private Timestamp createTime;
    private Timestamp completeTime;
}
