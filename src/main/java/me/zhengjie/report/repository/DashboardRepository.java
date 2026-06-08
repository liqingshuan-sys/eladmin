package me.zhengjie.report.repository;

import me.zhengjie.report.domain.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface DashboardRepository extends JpaRepository<Dashboard, Long>,
        JpaSpecificationExecutor<Dashboard> {
    List<Dashboard> findByCreateByOrderByCreateTimeDesc(String createBy);
}
