package com.efaturaai.core.repository;

import com.efaturaai.core.domain.AiErrorSample;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiErrorSampleRepository extends JpaRepository<AiErrorSample, UUID> {
}
