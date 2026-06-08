package me.zhengjie.workflow.dto;

import lombok.Data;
import java.sql.Timestamp;

/**
 * 流程定义 DTO
 */
@Data
public class ProcessDefinitionDto {
    private Long id;
    private String flowableDeployId;
    private String flowableProcDefId;
    private String name;
    private String description;
    private String processKey;
    private Integer version;
    private String bpmnFileName;
    private Integer status;
    private String createBy;
    private Timestamp createTime;
    private Timestamp updateTime;
}
