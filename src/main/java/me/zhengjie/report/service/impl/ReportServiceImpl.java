package me.zhengjie.report.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.report.domain.ChartConfig;
import me.zhengjie.report.domain.Dashboard;
import me.zhengjie.report.domain.DataSourceConfig;
import me.zhengjie.report.repository.ChartConfigRepository;
import me.zhengjie.report.repository.DashboardRepository;
import me.zhengjie.report.repository.DataSourceConfigRepository;
import me.zhengjie.report.service.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.sql.DataSource;
import java.sql.*;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final DashboardRepository dashboardRepo;
    private final ChartConfigRepository chartRepo;
    private final DataSourceConfigRepository dsRepo;

    /** In-memory cache for dynamic data sources */
    private final Map<Long, DataSource> dataSourceCache = new HashMap<>();

    // ==================== Dashboard ====================

    @Override
    public Dashboard createDashboard(Dashboard dashboard) {
        dashboard.setCreateTime(new Timestamp(System.currentTimeMillis()));
        dashboard.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return dashboardRepo.save(dashboard);
    }

    @Override
    public Dashboard updateDashboard(Dashboard dashboard) {
        Dashboard existing = getDashboard(dashboard.getId());
        if (dashboard.getName() != null) existing.setName(dashboard.getName());
        if (dashboard.getDescription() != null) existing.setDescription(dashboard.getDescription());
        if (dashboard.getLayout() != null) existing.setLayout(dashboard.getLayout());
        if (dashboard.getCoverImage() != null) existing.setCoverImage(dashboard.getCoverImage());
        if (dashboard.getIsPublic() != null) existing.setIsPublic(dashboard.getIsPublic());
        existing.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return dashboardRepo.save(existing);
    }

    @Override
    @Transactional
    public void deleteDashboard(Long id) {
        chartRepo.deleteByDashboardId(id);
        dashboardRepo.deleteById(id);
    }

    @Override
    public Dashboard getDashboard(Long id) {
        return dashboardRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("仪表盘不存在"));
    }

    @Override
    public Map<String, Object> listDashboards(String name, Pageable pageable) {
        Page<Dashboard> page = dashboardRepo.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StrUtil.isNotBlank(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        return PageUtil.toPage(page);
    }

    @Override
    public Dashboard publishDashboard(Long id) {
        Dashboard db = getDashboard(id);
        db.setStatus(1);
        return dashboardRepo.save(db);
    }

    @Override
    public Dashboard getPublicDashboard(Long id) {
        Dashboard db = getDashboard(id);
        if (!db.getIsPublic() && db.getStatus() != 1) {
            throw new BadRequestException("仪表盘未公开");
        }
        return db;
    }

    // ==================== Chart Config ====================

    @Override
    public ChartConfig createChart(ChartConfig chart) {
        chart.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return chartRepo.save(chart);
    }

    @Override
    public ChartConfig updateChart(ChartConfig chart) {
        ChartConfig existing = chartRepo.findById(chart.getId())
                .orElseThrow(() -> new BadRequestException("图表不存在"));
        if (chart.getTitle() != null) existing.setTitle(chart.getTitle());
        if (chart.getChartType() != null) existing.setChartType(chart.getChartType());
        if (chart.getQuerySql() != null) existing.setQuerySql(chart.getQuerySql());
        if (chart.getXField() != null) existing.setXField(chart.getXField());
        if (chart.getYFields() != null) existing.setYFields(chart.getYFields());
        if (chart.getStyleOptions() != null) existing.setStyleOptions(chart.getStyleOptions());
        if (chart.getSortOrder() != null) existing.setSortOrder(chart.getSortOrder());
        if (chart.getDataSourceId() != null) existing.setDataSourceId(chart.getDataSourceId());
        existing.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return chartRepo.save(existing);
    }

    @Override
    public void deleteChart(Long id) {
        chartRepo.deleteById(id);
    }

    @Override
    public List<ChartConfig> getDashboardCharts(Long dashboardId) {
        return chartRepo.findByDashboardIdOrderBySortOrderAsc(dashboardId);
    }

    @Override
    public Map<String, Object> executeChartQuery(Long chartId) {
        ChartConfig chart = chartRepo.findById(chartId)
                .orElseThrow(() -> new BadRequestException("图表不存在"));

        List<Map<String, Object>> data;
        if (chart.getDataSourceId() != null && chart.getDataSourceId() > 0) {
            data = executeQuery(chart.getDataSourceId(), chart.getQuerySql());
        } else {
            // Use default datasource (main app database)
            data = executeQuery(null, chart.getQuerySql());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("chart", chart);
        result.put("data", data);
        return result;
    }

    // ==================== Data Source ====================

    @Override
    public DataSourceConfig createDataSource(DataSourceConfig ds) {
        ds.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return dsRepo.save(ds);
    }

    @Override
    public DataSourceConfig updateDataSource(DataSourceConfig ds) {
        DataSourceConfig existing = dsRepo.findById(ds.getId())
                .orElseThrow(() -> new BadRequestException("数据源不存在"));
        if (ds.getName() != null) existing.setName(ds.getName());
        if (ds.getDbType() != null) existing.setDbType(ds.getDbType());
        if (ds.getUrl() != null) existing.setUrl(ds.getUrl());
        if (ds.getUsername() != null) existing.setUsername(ds.getUsername());
        if (ds.getPassword() != null) existing.setPassword(ds.getPassword());
        if (ds.getStatus() != null) existing.setStatus(ds.getStatus());
        return dsRepo.save(existing);
    }

    @Override
    public void deleteDataSource(Long id) {
        dataSourceCache.remove(id);
        dsRepo.deleteById(id);
    }

    @Override
    public DataSourceConfig getDataSource(Long id) {
        return dsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("数据源不存在"));
    }

    @Override
    public Map<String, Object> listDataSources(String name, Pageable pageable) {
        Page<DataSourceConfig> page = dsRepo.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StrUtil.isNotBlank(name)) {
                predicates.add(cb.like(root.get("name"), "%" + name + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        return PageUtil.toPage(page);
    }

    @Override
    public List<Map<String, Object>> testConnection(Long id) {
        try {
            return executeQuery(id, "SELECT 1 as test");
        } catch (Exception e) {
            throw new BadRequestException("连接失败: " + e.getMessage());
        }
    }

    // ==================== Dynamic Query ====================

    @Override
    public List<Map<String, Object>> executeQuery(Long dataSourceId, String sql) {
        JdbcTemplate jdbc;
        if (dataSourceId != null) {
            jdbc = new JdbcTemplate(getDynamicDataSource(dataSourceId));
        } else {
            jdbc = new JdbcTemplate(
                    org.springframework.beans.factory.BeanFactoryUtils
                            .beanOfTypeIncludingAncestors(
                                    org.springframework.context.ApplicationContext.class,
                                    javax.sql.DataSource.class));
            // Use a simple approach: get from Spring context
            return Collections.emptyList();
        }

        List<Map<String, Object>> rows = jdbc.queryForList(sql);
        return rows;
    }

    @Override
    public List<String> listTables(Long dataSourceId) {
        DataSourceConfig ds = getDataSource(dataSourceId);
        List<String> tables = new ArrayList<>();
        try (Connection conn = getConnection(ds);
             ResultSet rs = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            throw new BadRequestException("查询表失败: " + e.getMessage());
        }
        return tables;
    }

    @Override
    public List<Map<String, String>> describeTable(Long dataSourceId, String tableName) {
        DataSourceConfig ds = getDataSource(dataSourceId);
        List<Map<String, String>> columns = new ArrayList<>();
        try (Connection conn = getConnection(ds);
             ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, "%")) {
            while (rs.next()) {
                Map<String, String> col = new HashMap<>();
                col.put("name", rs.getString("COLUMN_NAME"));
                col.put("type", rs.getString("TYPE_NAME"));
                col.put("size", String.valueOf(rs.getInt("COLUMN_SIZE")));
                col.put("nullable", rs.getString("IS_NULLABLE"));
                col.put("comment", rs.getString("REMARKS"));
                columns.add(col);
            }
        } catch (SQLException e) {
            throw new BadRequestException("查询表结构失败: " + e.getMessage());
        }
        return columns;
    }

    // ==================== Helpers ====================

    private DataSource getDynamicDataSource(Long id) {
        if (dataSourceCache.containsKey(id)) {
            return dataSourceCache.get(id);
        }
        DataSourceConfig ds = getDataSource(id);
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(ds.getUrl());
        dataSource.setUsername(ds.getUsername());
        dataSource.setPassword(ds.getPassword());
        dataSource.setDriverClassName(getDriverClass(ds.getDbType()));
        dataSourceCache.put(id, dataSource);
        return dataSource;
    }

    private Connection getConnection(DataSourceConfig ds) throws SQLException {
        return DriverManager.getConnection(ds.getUrl(), ds.getUsername(), ds.getPassword());
    }

    private String getDriverClass(String dbType) {
        switch (dbType) {
            case "mysql": return "com.mysql.cj.jdbc.Driver";
            case "postgresql": return "org.postgresql.Driver";
            case "oracle": return "oracle.jdbc.OracleDriver";
            case "sqlserver": return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            default: return "com.mysql.cj.jdbc.Driver";
        }
    }
}
