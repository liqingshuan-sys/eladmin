package me.zhengjie.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.workflow.domain.ProcessDefinition;
import me.zhengjie.workflow.domain.ProcessInstance;
import me.zhengjie.workflow.domain.WorkflowTask;
import me.zhengjie.workflow.dto.CompleteTaskDto;
import me.zhengjie.workflow.dto.DeployBpmnDto;
import me.zhengjie.workflow.service.WorkflowService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;

/**
 * 工作流引擎 REST API
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "工作流：流程管理")
@RequestMapping("/api/workflow")
public class WorkflowController {

    private final WorkflowService workflowService;

    // ========== 流程定义 ==========

    @PostMapping("/definitions/deploy")
    @Log("部署BPMN流程")
    @ApiOperation("部署BPMN流程（XML文本）")
    @PreAuthorize("@el.check('workflow:deploy')")
    public ResponseEntity<ProcessDefinition> deployBpmn(@Valid @RequestBody DeployBpmnDto dto) {
        return new ResponseEntity<>(
                workflowService.deployBpmn(dto.getName(), dto.getDescription(), dto.getProcessKey(), dto.getBpmnContent()),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/definitions/deploy-file")
    @Log("上传BPMN文件部署")
    @ApiOperation("上传BPMN文件部署")
    @PreAuthorize("@el.check('workflow:deploy')")
    public ResponseEntity<ProcessDefinition> deployBpmnFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String description) throws Exception {
        return new ResponseEntity<>(
                workflowService.deployBpmnFile(file, description),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/definitions")
    @ApiOperation("查询流程定义列表")
    @PreAuthorize("@el.check('workflow:list')")
    public ResponseEntity<Map<String, Object>> listProcessDefinitions(
            @RequestParam(required = false) String name, Pageable pageable) {
        return ResponseEntity.ok(workflowService.listProcessDefinitions(name, pageable));
    }

    @GetMapping("/definitions/{id}")
    @ApiOperation("获取流程定义详情")
    @PreAuthorize("@el.check('workflow:list')")
    public ResponseEntity<ProcessDefinition> getProcessDefinition(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getProcessDefinition(id));
    }

    @PutMapping("/definitions/{id}/suspend")
    @Log("挂起/激活流程定义")
    @ApiOperation("挂起/激活流程定义")
    @PreAuthorize("@el.check('workflow:edit')")
    public ResponseEntity<Void> suspendProcessDefinition(@PathVariable Long id,
                                                          @RequestParam boolean suspend) {
        workflowService.suspendProcessDefinition(id, suspend);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/definitions/{id}")
    @Log("删除流程定义")
    @ApiOperation("删除流程定义")
    @PreAuthorize("@el.check('workflow:del')")
    public ResponseEntity<Void> deleteProcessDefinition(@PathVariable Long id) {
        workflowService.deleteProcessDefinition(id);
        return ResponseEntity.noContent().build();
    }

    // ========== 流程实例 ==========

    @PostMapping("/instances/start")
    @Log("启动流程实例")
    @ApiOperation("启动流程实例")
    @PreAuthorize("@el.check('workflow:start')")
    public ResponseEntity<ProcessInstance> startProcessInstance(
            @RequestParam Long procDefId,
            @RequestParam(required = false) String businessKey,
            @RequestBody(required = false) Map<String, Object> variables) {
        return new ResponseEntity<>(
                workflowService.startProcessInstance(procDefId, businessKey, variables),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/instances")
    @ApiOperation("查询流程实例列表")
    @PreAuthorize("@el.check('workflow:list')")
    public ResponseEntity<Map<String, Object>> listProcessInstances(
            @RequestParam(required = false) String processName,
            @RequestParam(required = false) Integer status,
            Pageable pageable) {
        return ResponseEntity.ok(workflowService.listProcessInstances(processName, status, pageable));
    }

    @GetMapping("/instances/{id}")
    @ApiOperation("获取流程实例详情")
    @PreAuthorize("@el.check('workflow:list')")
    public ResponseEntity<ProcessInstance> getProcessInstance(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getProcessInstance(id));
    }

    @PutMapping("/instances/{id}/suspend")
    @Log("挂起/激活流程实例")
    @ApiOperation("挂起/激活流程实例")
    @PreAuthorize("@el.check('workflow:edit')")
    public ResponseEntity<Void> suspendProcessInstance(@PathVariable Long id,
                                                        @RequestParam boolean suspend) {
        workflowService.suspendProcessInstance(id, suspend);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/instances/{id}/terminate")
    @Log("终止流程实例")
    @ApiOperation("终止流程实例")
    @PreAuthorize("@el.check('workflow:edit')")
    public ResponseEntity<Void> terminateProcessInstance(@PathVariable Long id) {
        workflowService.terminateProcessInstance(id);
        return ResponseEntity.noContent().build();
    }

    // ========== 任务管理 ==========

    @GetMapping("/tasks/my")
    @ApiOperation("查询我的待办任务")
    @PreAuthorize("@el.check('workflow:list')")
    public ResponseEntity<Map<String, Object>> listMyTasks(
            @RequestParam String assignee,
            @RequestParam(required = false) String taskName,
            Pageable pageable) {
        return ResponseEntity.ok(workflowService.listMyTasks(assignee, taskName, pageable));
    }

    @GetMapping("/tasks/completed")
    @ApiOperation("查询我的已办任务")
    @PreAuthorize("@el.check('workflow:list')")
    public ResponseEntity<Map<String, Object>> listCompletedTasks(
            @RequestParam String assignee, Pageable pageable) {
        return ResponseEntity.ok(workflowService.listCompletedTasks(assignee, pageable));
    }

    @GetMapping("/tasks/{id}")
    @ApiOperation("获取任务详情")
    @PreAuthorize("@el.check('workflow:list')")
    public ResponseEntity<WorkflowTask> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getTask(id));
    }

    @PostMapping("/tasks/{id}/claim")
    @Log("认领任务")
    @ApiOperation("认领任务")
    @PreAuthorize("@el.check('workflow:edit')")
    public ResponseEntity<Void> claimTask(@PathVariable Long id, @RequestParam String userId) {
        workflowService.claimTask(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tasks/{id}/complete")
    @Log("完成任务")
    @ApiOperation("完成任务")
    @PreAuthorize("@el.check('workflow:edit')")
    public ResponseEntity<Void> completeTask(@PathVariable Long id, @Valid @RequestBody CompleteTaskDto dto) {
        workflowService.completeTask(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tasks/{id}/reject")
    @Log("驳回任务")
    @ApiOperation("驳回任务")
    @PreAuthorize("@el.check('workflow:edit')")
    public ResponseEntity<Void> rejectTask(@PathVariable Long id, @RequestParam String comment) {
        workflowService.rejectTask(id, comment);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tasks/{id}/delegate")
    @Log("委托任务")
    @ApiOperation("委托任务")
    @PreAuthorize("@el.check('workflow:edit')")
    public ResponseEntity<Void> delegateTask(@PathVariable Long id, @RequestParam String delegateUserId) {
        workflowService.delegateTask(id, delegateUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/count")
    @ApiOperation("获取待办任务数量")
    public ResponseEntity<Long> getPendingTaskCount(@RequestParam String userId) {
        return ResponseEntity.ok(workflowService.getPendingTaskCount(userId));
    }
}
