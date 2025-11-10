package com.efaturaai.core.repository;

import com.efaturaai.core.domain.Product;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, UUID> {
  @Query("select p from Product p where p.sku = :sku and p.tenantId = :tenantId")
  Optional<Product> findBySkuAndTenant(
      @Param("sku") String sku, @Param("tenantId") UUID tenantId);
}

