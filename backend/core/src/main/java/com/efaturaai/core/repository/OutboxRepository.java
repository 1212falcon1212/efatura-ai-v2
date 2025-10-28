package com.efaturaai.core.repository;

import com.efaturaai.core.domain.OutboxMessage;
import com.efaturaai.core.domain.OutboxStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {

  @Query(
      "select o from OutboxMessage o where o.status = :status and (o.nextRetryAt is null or o.nextRetryAt <= :now) and o.tenantId = :tenantId order by o.createdAt asc")
  List<OutboxMessage> findReady(
      @Param("status") OutboxStatus status,
      @Param("now") OffsetDateTime now,
      @Param("tenantId") UUID tenantId);

  @Modifying
  @Query("update OutboxMessage o set o.status = :status, o.retryCount = :retry where o.id = :id")
  int updateStatus(
      @Param("id") UUID id, @Param("status") OutboxStatus status, @Param("retry") int retry);

  @Modifying
  @Query("update OutboxMessage o set o.processed = true where o.id = :id")
  int markProcessed(@Param("id") UUID id);
}
