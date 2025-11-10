package com.efaturaai.core.domain;

import com.efaturaai.core.tenant.TenantAware;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhooks")
public class WebhookSubscription implements TenantAware {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "event_type", nullable = false, length = 100)
  private String eventType;

  @Column(name = "url", nullable = false, length = 500)
  private String url;

  @Column(name = "secret", nullable = false, length = 200)
  private String secret;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  @Override public UUID getTenantId() { return tenantId; }
  @Override public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
  public String getEventType() { return eventType; }
  public void setEventType(String eventType) { this.eventType = eventType; }
  public String getUrl() { return url; }
  public void setUrl(String url) { this.url = url; }
  public String getSecret() { return secret; }
  public void setSecret(String secret) { this.secret = secret; }
  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}


