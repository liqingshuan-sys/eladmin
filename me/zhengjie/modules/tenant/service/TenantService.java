package me.zhengjie.modules.tenant.service;

import me.zhengjie.modules.tenant.domain.Tenant;
import me.zhengjie.modules.tenant.domain.TenantUser;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface TenantService {

    Tenant createTenant(Tenant tenant);
    Tenant updateTenant(Tenant tenant);
    void deleteTenant(Long id);
    Tenant getTenant(Long id);
    Tenant getTenantByCode(String tenantCode);
    Map<String, Object> listTenants(String name, Pageable pageable);

    // Tenant user management
    void addUserToTenant(Long tenantId, Long userId, String role);
    void removeUserFromTenant(Long tenantId, Long userId);
    List<TenantUser> getTenantUsers(Long tenantId);
    Long getUserCount(Long tenantId);

    // Switch tenant context
    void switchTenant(String tenantCode);
    void clearTenantContext();

    // Validation
    boolean isTenantFull(Long tenantId);
}
