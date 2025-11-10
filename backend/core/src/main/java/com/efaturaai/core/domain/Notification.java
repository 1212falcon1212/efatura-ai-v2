package com.efaturaai.core.domain;

import com.efaturaai.core.tenant.TenantAware;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "notifications")
public class Notification implements TenantAware {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "type", nullable = false, length = 100)
  private String type;

  @Column(name = "channel", nullable = false, length = 50)
  private String channel;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "payload", nullable = false, columnDefinition = "JSONB")
  private String payload;

  @Column(name = "status", nullable = false, length = 30)
  private String status;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "sent_at")
  private OffsetDateTime sentAt;

  @Column(name = "read_at")
  private OffsetDateTime readAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  @Override public UUID getTenantId() { return tenantId; }
  @Override public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getChannel() { return channel; }
  public void setChannel(String channel) { this.channel = channel; }
  public String getPayload() { return payload; }
  public void setPayload(String payload) { this.payload = payload; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
  public OffsetDateTime getSentAt() { return sentAt; }
  public void setSentAt(OffsetDateTime sentAt) { this.sentAt = sentAt; }
  public OffsetDateTime getReadAt() { return readAt; }
  public void setReadAt(OffsetDateTime readAt) { this.readAt = readAt; }
}


