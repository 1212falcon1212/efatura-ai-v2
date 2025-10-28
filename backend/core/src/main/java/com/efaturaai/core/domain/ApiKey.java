package com.efaturaai.core.domain;

import com.efaturaai.core.tenant.TenantAware;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "api_keys")
public class ApiKey implements TenantAware {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "name", nullable = false, length = 120)
  private String name;

  @Column(name = "api_key", nullable = false, length = 128, unique = true)
  private String apiKey;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "scopes", columnDefinition = "JSONB")
  private String scopes;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  @Override public UUID getTenantId() { return tenantId; }
  @Override public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getApiKey() { return apiKey; }
  public void setApiKey(String apiKey) { this.apiKey = apiKey; }
  public String getScopes() { return scopes; }
  public void setScopes(String scopes) { this.scopes = scopes; }
  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}


