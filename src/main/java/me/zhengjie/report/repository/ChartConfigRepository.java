package me.zhengjie.report.repository;

import me.zhengjie.report.domain.ChartConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChartConfigRepository extends JpaRepository<ChartConfig, Long> {
    List<ChartConfig> findByDashboardIdOrderBySortOrderAsc(Long dashboardId);
    void deleteByDashboardId(Long dashboardId);
}
