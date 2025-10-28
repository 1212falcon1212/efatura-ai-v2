package com.efaturaai.core.repository;

import com.efaturaai.core.domain.Customer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
  @Query("select c from Customer c where c.name = :name and c.tenantId = :tenantId")
  Optional<Customer> findByNameAndTenant(
      @Param("name") String name, @Param("tenantId") UUID tenantId);
}
