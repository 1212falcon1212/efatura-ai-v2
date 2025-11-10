package com.efaturaai.core.domain;

import com.efaturaai.core.tenant.TenantAware;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_retry_policies")
public class AiRetryPolicy {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "error_type", nullable = false, unique = true, length = 50)
  private String errorType;

  @Column(name = "max_attempts", nullable = false)
  private Integer maxAttempts = 3;

  @Column(name = "backoff_initial_ms", nullable = false)
  private Long backoffInitialMs = 1000L;

  @Column(name = "backoff_multiplier", nullable = false, precision = 5, scale = 2)
  private BigDecimal backoffMultiplier = new BigDecimal("2.0");

  @Column(name = "jitter_ms", nullable = false)
  private Long jitterMs = 100L;

  @Column(name = "enabled", nullable = false)
  private Boolean enabled = true;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getErrorType() {
    return errorType;
  }

  public void setErrorType(String errorType) {
    this.errorType = errorType;
  }

  public Integer getMaxAttempts() {
    return maxAttempts;
  }

  public void setMaxAttempts(Integer maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  public Long getBackoffInitialMs() {
    return backoffInitialMs;
  }

  public void setBackoffInitialMs(Long backoffInitialMs) {
    this.backoffInitialMs = backoffInitialMs;
  }

  public BigDecimal getBackoffMultiplier() {
    return backoffMultiplier;
  }

  public void setBackoffMultiplier(BigDecimal backoffMultiplier) {
    this.backoffMultiplier = backoffMultiplier;
  }

  public Long getJitterMs() {
    return jitterMs;
  }

  public void setJitterMs(Long jitterMs) {
    this.jitterMs = jitterMs;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(OffsetDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
