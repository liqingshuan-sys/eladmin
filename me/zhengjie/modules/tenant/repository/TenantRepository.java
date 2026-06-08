package me.zhengjie.modules.tenant.repository;

import me.zhengjie.modules.tenant.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long>,
        JpaSpecificationExecutor<Tenant> {
    Optional<Tenant> findByTenantCode(String tenantCode);
    boolean existsByTenantCode(String tenantCode);
}
