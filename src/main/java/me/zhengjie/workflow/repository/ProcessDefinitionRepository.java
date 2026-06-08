package me.zhengjie.workflow.repository;

import me.zhengjie.workflow.domain.ProcessDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface ProcessDefinitionRepository extends JpaRepository<ProcessDefinition, Long>,
        JpaSpecificationExecutor<ProcessDefinition> {
    Optional<ProcessDefinition> findByFlowableProcDefId(String flowableProcDefId);
    Optional<ProcessDefinition> findByProcessKey(String processKey);
    boolean existsByProcessKey(String processKey);
}
