package com.efaturaai.core.repository;

import com.efaturaai.core.domain.ApiKey;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
  @Query("select k from ApiKey k where k.apiKey = :key and k.active = true")
  Optional<ApiKey> findActiveByKey(@Param("key") String key);
}


