package me.zhengjie.workflow.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.workflow.domain.ProcessDefinition;
import me.zhengjie.workflow.domain.ProcessInstance;
import me.zhengjie.workflow.domain.WorkflowTask;
import me.zhengjie.workflow.dto.CompleteTaskDto;
import me.zhengjie.workflow.repository.ProcessDefinitionRepository;
import me.zhengjie.workflow.repository.ProcessInstanceRepository;
import me.zhengjie.workflow.repository.WorkflowTaskRepository;
import me.zhengjie.workflow.service.WorkflowService;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final ProcessDefinitionRepository procDefRepo;
    private final ProcessInstanceRepository procInstRepo;
    private final WorkflowTaskRepository taskRepo;
    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessDefinition deployBpmn(String name, String description, String processKey, String bpmnXml) {
        // Deploy to Flowable
        org.flowable.engine.repository.Deployment deployment = repositoryService.createDeployment()
                .name(name)
                .category(description)
                .addInputStream(processKey + ".bpmn20.xml",
                        new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)))
                .deploy();

        // Get the process definition
        org.flowable.engine.repository.ProcessDefinition flowableProcDef =
                repositoryService.createProcessDefinitionQuery()
                        .deploymentId(deployment.getId())
                        .singleResult();

        // Save to local table
        ProcessDefinition entity = new ProcessDefinition();
        entity.setName(name);
        entity.setDescription(description);
        entity.setProcessKey(processKey);
        entity.setFlowableDeployId(deployment.getId());
        entity.setFlowableProcDefId(flowableProcDef.getId());
        entity.setVersion(flowableProcDef.getVersion());
        entity.setBpmnFileName(processKey + ".bpmn20.xml");
        entity.setStatus(0);
        entity.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));

        return procDefRepo.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessDefinition deployBpmnFile(MultipartFile file, String description) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".bpmn") && !fileName.endsWith(".bpmn20.xml")) {
            throw new BadRequestException("仅支持 .bpmn / .bpmn20.xml 文件");
        }
        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        return deployBpmn(
                fileName.replace(".bpmn20.xml", "").replace(".bpmn", ""),
                description,
                "process_" + System.currentTimeMillis(),
                content
        );
    }

    @Override
    public Map<String, Object> listProcessDefinitions(String name, Pageable pageable) {
        Page<ProcessDefinition> page = procDefRepo.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StrUtil.isNotBlank(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        return PageUtil.toPage(page);
    }

    @Override
    public ProcessDefinition getProcessDefinition(Long id) {
        ProcessDefinition def = procDefRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("流程定义不存在"));
        return def;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void suspendProcessDefinition(Long id, boolean suspend) {
        ProcessDefinition def = getProcessDefinition(id);
        if (suspend) {
            repositoryService.suspendProcessDefinitionById(def.getFlowableProcDefId());
            def.setStatus(1);
        } else {
            repositoryService.activateProcessDefinitionById(def.getFlowableProcDefId());
            def.setStatus(0);
        }
        procDefRepo.save(def);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProcessDefinition(Long id) {
        ProcessDefinition def = getProcessDefinition(id);
        repositoryService.deleteDeployment(def.getFlowableDeployId(), true);
        procDefRepo.delete(def);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessInstance startProcessInstance(Long procDefId, String businessKey, Map<String, Object> variables) {
        ProcessDefinition def = getProcessDefinition(procDefId);

        if (variables == null) variables = new HashMap<>();
        org.flowable.engine.runtime.ProcessInstance flowableInst =
                runtimeService.startProcessInstanceById(def.getFlowableProcDefId(), businessKey, variables);

        ProcessInstance inst = new ProcessInstance();
        inst.setFlowableProcInstId(flowableInst.getId());
        inst.setProcDefId(procDefId);
        inst.setProcessName(def.getName());
        inst.setBusinessKey(businessKey);
        inst.setStarter(Optional.ofNullable(variables.get("starter")).map(Object::toString).orElse("system"));
        inst.setStatus(0);
        inst.setStartTime(Timestamp.valueOf(LocalDateTime.now()));

        ProcessInstance saved = procInstRepo.save(inst);

        // Sync tasks
        syncTasks(flowableInst.getId(), saved.getId());

        return saved;
    }

    @Override
    public Map<String, Object> listProcessInstances(String processName, Integer status, Pageable pageable) {
        Page<ProcessInstance> page = procInstRepo.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StrUtil.isNotBlank(processName)) {
                predicates.add(cb.like(root.get("processName"), "%" + processName + "%"));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        return PageUtil.toPage(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void suspendProcessInstance(Long id, boolean suspend) {
        ProcessInstance inst = getProcessInstance(id);
        if (suspend) {
            runtimeService.suspendProcessInstanceById(inst.getFlowableProcInstId());
            inst.setStatus(2);
        } else {
            runtimeService.activateProcessInstanceById(inst.getFlowableProcInstId());
            inst.setStatus(0);
        }
        procInstRepo.save(inst);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void terminateProcessInstance(Long id) {
        ProcessInstance inst = getProcessInstance(id);
        runtimeService.deleteProcessInstance(inst.getFlowableProcInstId(), "用户手动终止");
        inst.setStatus(3);
        inst.setEndTime(Timestamp.valueOf(LocalDateTime.now()));
        procInstRepo.save(inst);
    }

    @Override
    public ProcessInstance getProcessInstance(Long id) {
        return procInstRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("流程实例不存在"));
    }

    @Override
    public Map<String, Object> listMyTasks(String assignee, String taskName, Pageable pageable) {
        Page<WorkflowTask> page = taskRepo.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("assignee"), assignee));
            predicates.add(cb.equal(root.get("status"), 0));
            if (StrUtil.isNotBlank(taskName)) {
                predicates.add(cb.like(root.get("taskName"), "%" + taskName + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        return PageUtil.toPage(page);
    }

    @Override
    public Map<String, Object> listCompletedTasks(String assignee, Pageable pageable) {
        Page<WorkflowTask> page = taskRepo.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("assignee"), assignee));
            predicates.add(cb.equal(root.get("status"), 1));
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        return PageUtil.toPage(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimTask(Long taskId, String userId) {
        WorkflowTask task = taskRepo.findById(taskId)
                .orElseThrow(() -> new BadRequestException("任务不存在"));
        taskService.claim(task.getFlowableTaskId(), userId);
        task.setAssignee(userId);
        taskRepo.save(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(Long taskId, CompleteTaskDto dto) {
        WorkflowTask task = taskRepo.findById(taskId)
                .orElseThrow(() -> new BadRequestException("任务不存在"));

        Map<String, Object> vars = dto.getVariables();
        if (vars == null) vars = new HashMap<>();
        if (StrUtil.isNotBlank(dto.getComment())) {
            vars.put("comment", dto.getComment());
        }
        taskService.complete(task.getFlowableTaskId(), vars);

        task.setStatus(1);
        task.setComment(dto.getComment());
        task.setCompleteTime(Timestamp.valueOf(LocalDateTime.now()));
        taskRepo.save(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectTask(Long taskId, String comment) {
        WorkflowTask task = taskRepo.findById(taskId)
                .orElseThrow(() -> new BadRequestException("任务不存在"));
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(task.getFlowableTaskId())
                .moveToActivityId(task.getTaskDefKey(), getPreviousTaskDefKey(task))
                .changeState();
        task.setStatus(2);
        task.setComment(comment);
        task.setCompleteTime(Timestamp.valueOf(LocalDateTime.now()));
        taskRepo.save(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(Long taskId, String delegateUserId) {
        WorkflowTask task = taskRepo.findById(taskId)
                .orElseThrow(() -> new BadRequestException("任务不存在"));
        taskService.delegateTask(task.getFlowableTaskId(), delegateUserId);
        task.setAssignee(delegateUserId);
        taskRepo.save(task);
    }

    @Override
    public WorkflowTask getTask(Long taskId) {
        return taskRepo.findById(taskId)
                .orElseThrow(() -> new BadRequestException("任务不存在"));
    }

    @Override
    public Long getPendingTaskCount(String userId) {
        return taskRepo.countByAssigneeAndStatus(userId, 0);
    }

    // ========== Helper Methods ==========

    /**
     * 同步 Flowable 活跃任务到本地任务表
     */
    private void syncTasks(String flowableProcInstId, Long localProcInstId) {
        List<org.flowable.task.api.Task> activeTasks = taskService.createTaskQuery()
                .processInstanceId(flowableProcInstId)
                .active()
                .list();

        for (org.flowable.task.api.Task ft : activeTasks) {
            WorkflowTask wt = new WorkflowTask();
            wt.setFlowableTaskId(ft.getId());
            wt.setProcInstId(localProcInstId);
            wt.setTaskName(ft.getName());
            wt.setTaskDefKey(ft.getTaskDefinitionKey());
            wt.setAssignee(ft.getAssignee());
            wt.setStatus(0);
            wt.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
            taskRepo.save(wt);
        }
    }

    /**
     * 获取上一步的任务定义 Key（简单实现）
     */
    private String getPreviousTaskDefKey(WorkflowTask task) {
        // In a real implementation, query the BPMN model for the previous node
        return "startEvent";
    }
}
