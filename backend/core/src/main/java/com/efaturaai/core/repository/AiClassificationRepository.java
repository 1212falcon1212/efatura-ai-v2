package com.efaturaai.core.repository;

import com.efaturaai.core.domain.AiClassification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiClassificationRepository extends JpaRepository<AiClassification, UUID> {
  Page<AiClassification> findByTenantId(UUID tenantId, Pageable pageable);
  List<AiClassification> findByTenantIdAndErrorType(UUID tenantId, String errorType);
}
