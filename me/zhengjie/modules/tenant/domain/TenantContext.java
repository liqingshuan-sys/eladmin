package me.zhengjie.modules.tenant.domain;

/**
 * 租户上下文 — 通过 ThreadLocal 传递当前租户 ID。
 * 配合 JPA Filter 实现数据自动隔离。
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<Long> CURRENT_TENANT_ID = new ThreadLocal<>();

    public static void setTenantCode(String tenantCode) {
        CURRENT_TENANT.set(tenantCode);
    }

    public static String getTenantCode() {
        return CURRENT_TENANT.get();
    }

    public static void setTenantId(Long tenantId) {
        CURRENT_TENANT_ID.set(tenantId);
    }

    public static Long getTenantId() {
        return CURRENT_TENANT_ID.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_TENANT_ID.remove();
    }
}
