package com.efaturaai.core.repository;

import com.efaturaai.core.domain.Role;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, UUID> {
  @Query("SELECT r FROM Role r WHERE r.tenantId = :tenantId AND r.name = :name")
  Optional<Role> findByTenantIdAndName(@Param("tenantId") UUID tenantId, @Param("name") String name);
}
