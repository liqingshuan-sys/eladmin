package me.zhengjie.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.report.domain.ChartConfig;
import me.zhengjie.report.domain.Dashboard;
import me.zhengjie.report.domain.DataSourceConfig;
import me.zhengjie.report.service.ReportService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Api(tags = "报表：数据大屏 & BI")
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;

    // ==================== Dashboard ====================

    @PostMapping("/dashboards")
    @Log("创建仪表盘")
    @ApiOperation("创建仪表盘")
    @PreAuthorize("@el.check('report:dashboard:add')")
    public ResponseEntity<Dashboard> createDashboard(@Valid @RequestBody Dashboard dashboard) {
        return new ResponseEntity<>(reportService.createDashboard(dashboard), HttpStatus.CREATED);
    }

    @PutMapping("/dashboards")
    @Log("修改仪表盘")
    @ApiOperation("修改仪表盘")
    @PreAuthorize("@el.check('report:dashboard:edit')")
    public ResponseEntity<Dashboard> updateDashboard(@RequestBody Dashboard dashboard) {
        return ResponseEntity.ok(reportService.updateDashboard(dashboard));
    }

    @DeleteMapping("/dashboards/{id}")
    @Log("删除仪表盘")
    @ApiOperation("删除仪表盘")
    @PreAuthorize("@el.check('report:dashboard:del')")
    public ResponseEntity<Void> deleteDashboard(@PathVariable Long id) {
        reportService.deleteDashboard(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboards/{id}")
    @ApiOperation("获取仪表盘详情")
    @PreAuthorize("@el.check('report:dashboard:list')")
    public ResponseEntity<Dashboard> getDashboard(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getDashboard(id));
    }

    @GetMapping("/dashboards")
    @ApiOperation("查询仪表盘列表")
    @PreAuthorize("@el.check('report:dashboard:list')")
    public ResponseEntity<Map<String, Object>> listDashboards(
            @RequestParam(required = false) String name, Pageable pageable) {
        return ResponseEntity.ok(reportService.listDashboards(name, pageable));
    }

    @PutMapping("/dashboards/{id}/publish")
    @Log("发布仪表盘")
    @ApiOperation("发布仪表盘")
    @PreAuthorize("@el.check('report:dashboard:edit')")
    public ResponseEntity<Dashboard> publishDashboard(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.publishDashboard(id));
    }

    @GetMapping("/dashboards/public/{id}")
    @ApiOperation("获取公开仪表盘（无需认证）")
    public ResponseEntity<Dashboard> getPublicDashboard(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getPublicDashboard(id));
    }

    // ==================== Chart ====================

    @PostMapping("/charts")
    @Log("创建图表")
    @ApiOperation("创建图表")
    @PreAuthorize("@el.check('report:chart:add')")
    public ResponseEntity<ChartConfig> createChart(@Valid @RequestBody ChartConfig chart) {
        return new ResponseEntity<>(reportService.createChart(chart), HttpStatus.CREATED);
    }

    @PutMapping("/charts")
    @Log("修改图表")
    @ApiOperation("修改图表")
    @PreAuthorize("@el.check('report:chart:edit')")
    public ResponseEntity<ChartConfig> updateChart(@RequestBody ChartConfig chart) {
        return ResponseEntity.ok(reportService.updateChart(chart));
    }

    @DeleteMapping("/charts/{id}")
    @Log("删除图表")
    @ApiOperation("删除图表")
    @PreAuthorize("@el.check('report:chart:del')")
    public ResponseEntity<Void> deleteChart(@PathVariable Long id) {
        reportService.deleteChart(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboards/{dashboardId}/charts")
    @ApiOperation("获取仪表盘下所有图表")
    @PreAuthorize("@el.check('report:chart:list')")
    public ResponseEntity<List<ChartConfig>> getDashboardCharts(@PathVariable Long dashboardId) {
        return ResponseEntity.ok(reportService.getDashboardCharts(dashboardId));
    }

    @GetMapping("/charts/{id}/query")
    @ApiOperation("执行图表数据查询")
    @PreAuthorize("@el.check('report:chart:query')")
    public ResponseEntity<Map<String, Object>> executeChartQuery(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.executeChartQuery(id));
    }

    // ==================== Data Source ====================

    @PostMapping("/datasources")
    @Log("创建数据源")
    @ApiOperation("创建数据源")
    @PreAuthorize("@el.check('report:ds:add')")
    public ResponseEntity<DataSourceConfig> createDataSource(@Valid @RequestBody DataSourceConfig ds) {
        return new ResponseEntity<>(reportService.createDataSource(ds), HttpStatus.CREATED);
    }

    @PutMapping("/datasources")
    @Log("修改数据源")
    @ApiOperation("修改数据源")
    @PreAuthorize("@el.check('report:ds:edit')")
    public ResponseEntity<DataSourceConfig> updateDataSource(@RequestBody DataSourceConfig ds) {
        return ResponseEntity.ok(reportService.updateDataSource(ds));
    }

    @DeleteMapping("/datasources/{id}")
    @Log("删除数据源")
    @ApiOperation("删除数据源")
    @PreAuthorize("@el.check('report:ds:del')")
    public ResponseEntity<Void> deleteDataSource(@PathVariable Long id) {
        reportService.deleteDataSource(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/datasources/{id}")
    @ApiOperation("获取数据源详情")
    @PreAuthorize("@el.check('report:ds:list')")
    public ResponseEntity<DataSourceConfig> getDataSource(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getDataSource(id));
    }

    @GetMapping("/datasources")
    @ApiOperation("查询数据源列表")
    @PreAuthorize("@el.check('report:ds:list')")
    public ResponseEntity<Map<String, Object>> listDataSources(
            @RequestParam(required = false) String name, Pageable pageable) {
        return ResponseEntity.ok(reportService.listDataSources(name, pageable));
    }

    @PostMapping("/datasources/{id}/test")
    @ApiOperation("测试数据源连接")
    @PreAuthorize("@el.check('report:ds:edit')")
    public ResponseEntity<List<Map<String, Object>>> testConnection(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.testConnection(id));
    }

    @GetMapping("/datasources/{id}/tables")
    @ApiOperation("获取数据源表列表")
    @PreAuthorize("@el.check('report:ds:list')")
    public ResponseEntity<List<String>> listTables(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.listTables(id));
    }

    @GetMapping("/datasources/{id}/tables/{tableName}")
    @ApiOperation("获取表结构")
    @PreAuthorize("@el.check('report:ds:list')")
    public ResponseEntity<List<Map<String, String>>> describeTable(
            @PathVariable Long id, @PathVariable String tableName) {
        return ResponseEntity.ok(reportService.describeTable(id, tableName));
    }

    @PostMapping("/query")
    @ApiOperation("执行自定义SQL查询")
    @PreAuthorize("@el.check('report:query')")
    public ResponseEntity<List<Map<String, Object>>> executeQuery(
            @RequestParam Long dataSourceId, @RequestParam String sql) {
        return ResponseEntity.ok(reportService.executeQuery(dataSourceId, sql));
    }
}
