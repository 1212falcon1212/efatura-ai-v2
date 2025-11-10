package com.efaturaai.core.repository;

import com.efaturaai.core.domain.AiRetryPolicy;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiRetryPolicyRepository extends JpaRepository<AiRetryPolicy, UUID> {
  Optional<AiRetryPolicy> findByErrorType(String errorType);
}
