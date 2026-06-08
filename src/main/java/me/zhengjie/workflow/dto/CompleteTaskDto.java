package me.zhengjie.workflow.dto;

import lombok.Data;
import java.util.Map;

/**
 * 完成任务请求 DTO
 */
@Data
public class CompleteTaskDto {
    private String comment;
    private Map<String, Object> variables;
}
