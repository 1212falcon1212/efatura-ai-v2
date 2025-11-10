package com.efaturaai.core.repository;

import com.efaturaai.core.domain.WebhookSubscription;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WebhookSubscriptionRepository extends JpaRepository<WebhookSubscription, UUID> {
  @Query("select w from WebhookSubscription w where w.tenantId = :tenantId and w.eventType = :event and w.active = true")
  List<WebhookSubscription> findActiveByTenantAndEvent(@Param("tenantId") UUID tenantId, @Param("event") String event);
}


