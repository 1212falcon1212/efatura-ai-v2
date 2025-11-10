package com.efaturaai.core.repository;

import com.efaturaai.core.domain.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  @Query("select u from User u where u.username = :username and u.tenantId = :tenantId")
  Optional<User> findByUsernameAndTenantId(
      @Param("username") String username, @Param("tenantId") UUID tenantId);
  
  @Query("select u from User u where u.tenantId = :tenantId")
  List<User> findByTenantId(@Param("tenantId") UUID tenantId);
}
