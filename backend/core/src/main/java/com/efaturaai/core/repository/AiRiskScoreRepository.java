package com.efaturaai.core.repository;

import com.efaturaai.core.domain.AiRiskScore;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRiskScoreRepository extends JpaRepository<AiRiskScore, UUID> {
  Optional<AiRiskScore> findByInvoiceId(UUID invoiceId);
}
