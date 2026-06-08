package me.zhengjie.workflow.service;

import me.zhengjie.workflow.domain.ProcessDefinition;
import me.zhengjie.workflow.domain.ProcessInstance;
import me.zhengjie.workflow.domain.WorkflowTask;
import me.zhengjie.workflow.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

/**
 * 工作流引擎服务接口
 */
public interface WorkflowService {

    // ========== 流程定义管理 ==========

    /** 部署 BPMN 流程定义 */
    ProcessDefinition deployBpmn(String name, String description, String processKey, String bpmnXml);

    /** 上传 BPMN 文件并部署 */
    ProcessDefinition deployBpmnFile(MultipartFile file, String description) throws Exception;

    /** 查询流程定义列表 */
    Map<String, Object> listProcessDefinitions(String name, Pageable pageable);

    /** 获取流程定义详情 */
    ProcessDefinition getProcessDefinition(Long id);

    /** 挂起/激活流程定义 */
    void suspendProcessDefinition(Long id, boolean suspend);

    /** 删除流程定义 */
    void deleteProcessDefinition(Long id);

    // ========== 流程实例管理 ==========

    /** 启动流程实例 */
    ProcessInstance startProcessInstance(Long procDefId, String businessKey, Map<String, Object> variables);

    /** 查询流程实例列表 */
    Map<String, Object> listProcessInstances(String processName, Integer status, Pageable pageable);

    /** 挂起/激活流程实例 */
    void suspendProcessInstance(Long id, boolean suspend);

    /** 终止流程实例 */
    void terminateProcessInstance(Long id);

    /** 获取流程实例详情 */
    ProcessInstance getProcessInstance(Long id);

    // ========== 任务管理 ==========

    /** 查询待办任务列表 */
    Map<String, Object> listMyTasks(String assignee, String taskName, Pageable pageable);

    /** 查询已办任务列表 */
    Map<String, Object> listCompletedTasks(String assignee, Pageable pageable);

    /** 认领任务 */
    void claimTask(Long taskId, String userId);

    /** 完成任务 */
    void completeTask(Long taskId, CompleteTaskDto dto);

    /** 驳回任务（退回上一步） */
    void rejectTask(Long taskId, String comment);

    /** 委托任务 */
    void delegateTask(Long taskId, String delegateUserId);

    /** 获取任务详情 */
    WorkflowTask getTask(Long taskId);

    /** 获取待办数量 */
    Long getPendingTaskCount(String userId);
}
