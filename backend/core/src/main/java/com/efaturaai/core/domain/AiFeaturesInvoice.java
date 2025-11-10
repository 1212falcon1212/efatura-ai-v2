package com.efaturaai.core.domain;

import com.efaturaai.core.tenant.TenantAware;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_features_invoice")
public class AiFeaturesInvoice implements TenantAware {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "invoice_id", nullable = false, unique = true)
  private UUID invoiceId;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "item_count", nullable = false)
  private Integer itemCount;

  @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal totalAmount;

  @Column(name = "previous_fail_rate", precision = 5, scale = 4)
  private BigDecimal previousFailRate = BigDecimal.ZERO;

  @Column(name = "endpoint_latency_ms")
  private Long endpointLatencyMs;

  @Column(name = "hour_of_day", nullable = false)
  private Integer hourOfDay;

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

  public Integer getItemCount() {
    return itemCount;
  }

  public void setItemCount(Integer itemCount) {
    this.itemCount = itemCount;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public BigDecimal getPreviousFailRate() {
    return previousFailRate;
  }

  public void setPreviousFailRate(BigDecimal previousFailRate) {
    this.previousFailRate = previousFailRate;
  }

  public Long getEndpointLatencyMs() {
    return endpointLatencyMs;
  }

  public void setEndpointLatencyMs(Long endpointLatencyMs) {
    this.endpointLatencyMs = endpointLatencyMs;
  }

  public Integer getHourOfDay() {
    return hourOfDay;
  }

  public void setHourOfDay(Integer hourOfDay) {
    this.hourOfDay = hourOfDay;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
