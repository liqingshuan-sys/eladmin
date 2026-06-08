package me.zhengjie.workflow.repository;

import me.zhengjie.workflow.domain.ProcessInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface ProcessInstanceRepository extends JpaRepository<ProcessInstance, Long>,
        JpaSpecificationExecutor<ProcessInstance> {
    Optional<ProcessInstance> findByFlowableProcInstId(String flowableProcInstId);
}
