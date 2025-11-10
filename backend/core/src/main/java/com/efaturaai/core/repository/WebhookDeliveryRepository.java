package com.efaturaai.core.repository;

import com.efaturaai.core.domain.WebhookDelivery;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, UUID> {
  @Query(
      "select d from WebhookDelivery d where d.status = 'FAILED' and d.attempt < 3 and (d.nextRetryAt is null or d.nextRetryAt <= :now) and d.tenantId = :tenantId")
  List<WebhookDelivery> findPending(@Param("tenantId") UUID tenantId, @Param("now") OffsetDateTime now);

  List<WebhookDelivery> findByWebhookIdAndTenantIdOrderByCreatedAtDesc(UUID webhookId, UUID tenantId);
}


