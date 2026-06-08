package me.zhengjie.modules.tenant.repository;

import me.zhengjie.modules.tenant.domain.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TenantUserRepository extends JpaRepository<TenantUser, Long> {
    List<TenantUser> findByTenantId(Long tenantId);
    Optional<TenantUser> findByUserId(Long userId);
    void deleteByUserId(Long userId);
    Long countByTenantId(Long tenantId);
}
