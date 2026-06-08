package me.zhengjie.modules.tenant.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.modules.tenant.domain.Tenant;
import me.zhengjie.modules.tenant.domain.TenantContext;
import me.zhengjie.modules.tenant.domain.TenantUser;
import me.zhengjie.modules.tenant.repository.TenantRepository;
import me.zhengjie.modules.tenant.repository.TenantUserRepository;
import me.zhengjie.modules.tenant.service.TenantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepo;
    private final TenantUserRepository tenantUserRepo;

    @Override
    @Transactional
    public Tenant createTenant(Tenant tenant) {
        if (tenantRepo.existsByTenantCode(tenant.getTenantCode())) {
            throw new BadRequestException("租户编码已存在");
        }
        tenant.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        tenant.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        return tenantRepo.save(tenant);
    }

    @Override
    @Transactional
    public Tenant updateTenant(Tenant tenant) {
        Tenant existing = getTenant(tenant.getId());
        if (tenant.getTenantName() != null) existing.setTenantName(tenant.getTenantName());
        if (tenant.getContactPerson() != null) existing.setContactPerson(tenant.getContactPerson());
        if (tenant.getContactPhone() != null) existing.setContactPhone(tenant.getContactPhone());
        if (tenant.getContactEmail() != null) existing.setContactEmail(tenant.getContactEmail());
        if (tenant.getAddress() != null) existing.setAddress(tenant.getAddress());
        if (tenant.getPlan() != null) existing.setPlan(tenant.getPlan());
        if (tenant.getMaxUsers() != null) existing.setMaxUsers(tenant.getMaxUsers());
        if (tenant.getMaxStorage() != null) existing.setMaxStorage(tenant.getMaxStorage());
        if (tenant.getExpireTime() != null) existing.setExpireTime(tenant.getExpireTime());
        if (tenant.getStatus() != null) existing.setStatus(tenant.getStatus());
        existing.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        return tenantRepo.save(existing);
    }

    @Override
    @Transactional
    public void deleteTenant(Long id) {
        tenantRepo.deleteById(id);
    }

    @Override
    public Tenant getTenant(Long id) {
        return tenantRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("租户不存在"));
    }

    @Override
    public Tenant getTenantByCode(String tenantCode) {
        return tenantRepo.findByTenantCode(tenantCode)
                .orElseThrow(() -> new BadRequestException("租户不存在"));
    }

    @Override
    public Map<String, Object> listTenants(String name, Pageable pageable) {
        Page<Tenant> page = tenantRepo.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StrUtil.isNotBlank(name)) {
                predicates.add(cb.like(root.get("tenantName"), "%" + name + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
        return PageUtil.toPage(page);
    }

    @Override
    @Transactional
    public void addUserToTenant(Long tenantId, Long userId, String role) {
        Tenant tenant = getTenant(tenantId);
        if (isTenantFull(tenantId)) {
            throw new BadRequestException("租户用户数已达上限");
        }
        TenantUser tu = new TenantUser();
        tu.setTenantId(tenantId);
        tu.setUserId(userId);
        tu.setRole(role != null ? role : "user");
        tu.setJoinTime(Timestamp.valueOf(LocalDateTime.now()));
        tenantUserRepo.save(tu);
    }

    @Override
    @Transactional
    public void removeUserFromTenant(Long tenantId, Long userId) {
        tenantUserRepo.deleteByUserId(userId);
    }

    @Override
    public List<TenantUser> getTenantUsers(Long tenantId) {
        return tenantUserRepo.findByTenantId(tenantId);
    }

    @Override
    public Long getUserCount(Long tenantId) {
        return tenantUserRepo.countByTenantId(tenantId);
    }

    @Override
    public void switchTenant(String tenantCode) {
        Tenant tenant = getTenantByCode(tenantCode);
        TenantContext.setTenantCode(tenantCode);
        TenantContext.setTenantId(tenant.getId());
    }

    @Override
    public void clearTenantContext() {
        TenantContext.clear();
    }

    @Override
    public boolean isTenantFull(Long tenantId) {
        Tenant tenant = getTenant(tenantId);
        Long count = getUserCount(tenantId);
        return count >= tenant.getMaxUsers();
    }
}
