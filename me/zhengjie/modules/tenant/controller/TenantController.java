package me.zhengjie.modules.tenant.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.tenant.domain.Tenant;
import me.zhengjie.modules.tenant.domain.TenantUser;
import me.zhengjie.modules.tenant.service.TenantService;
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
@Api(tags = "系统：多租户管理")
@RequestMapping("/api/tenant")
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @Log("创建租户")
    @ApiOperation("创建租户")
    @PreAuthorize("@el.check('tenant:add')")
    public ResponseEntity<Tenant> createTenant(@Valid @RequestBody Tenant tenant) {
        return new ResponseEntity<>(tenantService.createTenant(tenant), HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改租户")
    @ApiOperation("修改租户")
    @PreAuthorize("@el.check('tenant:edit')")
    public ResponseEntity<Tenant> updateTenant(@RequestBody Tenant tenant) {
        return ResponseEntity.ok(tenantService.updateTenant(tenant));
    }

    @DeleteMapping("/{id}")
    @Log("删除租户")
    @ApiOperation("删除租户")
    @PreAuthorize("@el.check('tenant:del')")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @ApiOperation("获取租户详情")
    @PreAuthorize("@el.check('tenant:list')")
    public ResponseEntity<Tenant> getTenant(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.getTenant(id));
    }

    @GetMapping
    @ApiOperation("查询租户列表")
    @PreAuthorize("@el.check('tenant:list')")
    public ResponseEntity<Map<String, Object>> listTenants(
            @RequestParam(required = false) String name, Pageable pageable) {
        return ResponseEntity.ok(tenantService.listTenants(name, pageable));
    }

    @PostMapping("/switch/{tenantCode}")
    @Log("切换租户上下文")
    @ApiOperation("切换当前租户上下文")
    @PreAuthorize("@el.check('tenant:edit')")
    public ResponseEntity<Void> switchTenant(@PathVariable String tenantCode) {
        tenantService.switchTenant(tenantCode);
        return ResponseEntity.noContent().build();
    }

    // Tenant user management
    @GetMapping("/{tenantId}/users")
    @ApiOperation("获取租户用户列表")
    @PreAuthorize("@el.check('tenant:list')")
    public ResponseEntity<List<TenantUser>> getTenantUsers(@PathVariable Long tenantId) {
        return ResponseEntity.ok(tenantService.getTenantUsers(tenantId));
    }

    @PostMapping("/{tenantId}/users/{userId}")
    @Log("添加用户到租户")
    @ApiOperation("添加用户到租户")
    @PreAuthorize("@el.check('tenant:edit')")
    public ResponseEntity<Void> addUserToTenant(
            @PathVariable Long tenantId,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "user") String role) {
        tenantService.addUserToTenant(tenantId, userId, role);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{tenantId}/users/{userId}")
    @Log("从租户移除用户")
    @ApiOperation("从租户移除用户")
    @PreAuthorize("@el.check('tenant:edit')")
    public ResponseEntity<Void> removeUserFromTenant(
            @PathVariable Long tenantId, @PathVariable Long userId) {
        tenantService.removeUserFromTenant(tenantId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tenantId}/users/count")
    @ApiOperation("获取租户用户数")
    public ResponseEntity<Long> getUserCount(@PathVariable Long tenantId) {
        return ResponseEntity.ok(tenantService.getUserCount(tenantId));
    }
}
