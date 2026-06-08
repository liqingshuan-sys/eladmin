package me.zhengjie.workflow.repository;

import me.zhengjie.workflow.domain.WorkflowTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface WorkflowTaskRepository extends JpaRepository<WorkflowTask, Long>,
        JpaSpecificationExecutor<WorkflowTask> {
    List<WorkflowTask> findByAssigneeAndStatusOrderByCreateTimeDesc(String assignee, Integer status);
    List<WorkflowTask> findByProcInstId(Long procInstId);
    Long countByAssigneeAndStatus(String assignee, Integer status);
}
