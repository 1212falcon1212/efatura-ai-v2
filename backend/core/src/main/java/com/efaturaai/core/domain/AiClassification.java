package com.efaturaai.core.domain;

import com.efaturaai.core.tenant.TenantAware;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_classifications")
public class AiClassification implements TenantAware {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "error_sample_id", nullable = false)
  private UUID errorSampleId;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "error_type", nullable = false, length = 50)
  private String errorType;

  @Column(name = "confidence", nullable = false, precision = 5, scale = 4)
  private BigDecimal confidence;

  @Column(name = "suggested_action", nullable = false, length = 50)
  private String suggestedAction;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getErrorSampleId() {
    return errorSampleId;
  }

  public void setErrorSampleId(UUID errorSampleId) {
    this.errorSampleId = errorSampleId;
  }

  @Override
  public UUID getTenantId() {
    return tenantId;
  }

  @Override
  public void setTenantId(UUID tenantId) {
    this.tenantId = tenantId;
  }

  public String getErrorType() {
    return errorType;
  }

  public void setErrorType(String errorType) {
    this.errorType = errorType;
  }

  public BigDecimal getConfidence() {
    return confidence;
  }

  public void setConfidence(BigDecimal confidence) {
    this.confidence = confidence;
  }

  public String getSuggestedAction() {
    return suggestedAction;
  }

  public void setSuggestedAction(String suggestedAction) {
    this.suggestedAction = suggestedAction;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
