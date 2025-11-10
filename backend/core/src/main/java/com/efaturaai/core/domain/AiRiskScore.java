package com.efaturaai.core.domain;

import com.efaturaai.core.tenant.TenantAware;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_risk_scores")
public class AiRiskScore implements TenantAware {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "invoice_id", nullable = false, unique = true)
  private UUID invoiceId;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "score_0_100", nullable = false)
  private Integer score0100;

  @Column(name = "model_version", nullable = false, length = 50)
  private String modelVersion = "v1.0";

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getInvoiceId() {
    return invoiceId;
  }

  public void setInvoiceId(UUID invoiceId) {
    this.invoiceId = invoiceId;
  }

  @Override
  public UUID getTenantId() {
    return tenantId;
  }

  @Override
  public void setTenantId(UUID tenantId) {
    this.tenantId = tenantId;
  }

  public Integer getScore0100() {
    return score0100;
  }

  public void setScore0100(Integer score0100) {
    this.score0100 = score0100;
  }

  public String getModelVersion() {
    return modelVersion;
  }

  public void setModelVersion(String modelVersion) {
    this.modelVersion = modelVersion;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
