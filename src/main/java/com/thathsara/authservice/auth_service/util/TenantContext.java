package com.thathsara.authservice.auth_service.util;

public class TenantContext {
    private static final ThreadLocal<Long> currentTenant = new ThreadLocal<>();

    public static void setTenantID(Long tenantId) {
        currentTenant.set(tenantId);
    }

    public static Long getTenantId() {
        return currentTenant.get();
    }

    public static void  clear() {
        currentTenant.remove();
    }
}
