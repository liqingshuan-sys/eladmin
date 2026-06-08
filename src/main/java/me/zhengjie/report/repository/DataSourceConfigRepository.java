package me.zhengjie.report.repository;

import me.zhengjie.report.domain.DataSourceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataSourceConfigRepository extends JpaRepository<DataSourceConfig, Long>,
        JpaSpecificationExecutor<DataSourceConfig> {
}
