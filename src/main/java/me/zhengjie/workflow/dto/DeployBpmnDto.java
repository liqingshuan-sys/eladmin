package me.zhengjie.workflow.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * BPMN 部署请求 DTO
 */
@Data
public class DeployBpmnDto {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String processKey;
    /** Base64 编码的 BPMN XML 内容 */
    @NotBlank
    private String bpmnContent;
}
