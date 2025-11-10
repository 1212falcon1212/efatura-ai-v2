package com.efaturaai.core.domain;

import com.efaturaai.core.tenant.TenantAware;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "webhook_deliveries")
public class WebhookDelivery implements TenantAware {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "webhook_id", nullable = false)
  private UUID webhookId;

  @Column(name = "event_type", nullable = false, length = 100)
  private String eventType;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "payload", nullable = false, columnDefinition = "JSONB")
  private String payload;

  @Column(name = "status", nullable = false, length = 30)
  private String status;

  @Column(name = "attempt", nullable = false)
  private int attempt;

  @Column(name = "next_retry_at")
  private OffsetDateTime nextRetryAt;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  @Override public UUID getTenantId() { return tenantId; }
  @Override public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
  public UUID getWebhookId() { return webhookId; }
  public void setWebhookId(UUID webhookId) { this.webhookId = webhookId; }
  public String getEventType() { return eventType; }
  public void setEventType(String eventType) { this.eventType = eventType; }
  public String getPayload() { return payload; }
  public void setPayload(String payload) { this.payload = payload; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public int getAttempt() { return attempt; }
  public void setAttempt(int attempt) { this.attempt = attempt; }
  public OffsetDateTime getNextRetryAt() { return nextRetryAt; }
  public void setNextRetryAt(OffsetDateTime nextRetryAt) { this.nextRetryAt = nextRetryAt; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}


