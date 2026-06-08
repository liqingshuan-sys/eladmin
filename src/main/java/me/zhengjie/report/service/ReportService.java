package me.zhengjie.report.service;

import me.zhengjie.report.domain.ChartConfig;
import me.zhengjie.report.domain.Dashboard;
import me.zhengjie.report.domain.DataSourceConfig;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface ReportService {

    // ===== 仪表盘 =====
    Dashboard createDashboard(Dashboard dashboard);
    Dashboard updateDashboard(Dashboard dashboard);
    void deleteDashboard(Long id);
    Dashboard getDashboard(Long id);
    Map<String, Object> listDashboards(String name, Pageable pageable);
    Dashboard publishDashboard(Long id);
    Dashboard getPublicDashboard(Long id);

    // ===== 图表配置 =====
    ChartConfig createChart(ChartConfig chart);
    ChartConfig updateChart(ChartConfig chart);
    void deleteChart(Long id);
    List<ChartConfig> getDashboardCharts(Long dashboardId);
    Map<String, Object> executeChartQuery(Long chartId);

    // ===== 数据源 =====
    DataSourceConfig createDataSource(DataSourceConfig ds);
    DataSourceConfig updateDataSource(DataSourceConfig ds);
    void deleteDataSource(Long id);
    DataSourceConfig getDataSource(Long id);
    Map<String, Object> listDataSources(String name, Pageable pageable);
    List<Map<String, Object>> testConnection(Long id);

    // ===== 动态查询 =====
    List<Map<String, Object>> executeQuery(Long dataSourceId, String sql);
    List<String> listTables(Long dataSourceId);
    List<Map<String, String>> describeTable(Long dataSourceId, String tableName);
}
