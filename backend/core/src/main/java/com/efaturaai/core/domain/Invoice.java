package com.efaturaai.core.domain;

import com.efaturaai.core.tenant.TenantAware;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
public class Invoice implements TenantAware {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "invoice_no", nullable = false, length = 64)
  private String invoiceNo;

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(name = "issue_date", nullable = false)
  private LocalDate issueDate;

  @Column(name = "currency", nullable = false, length = 3)
  private String currency = "TRY";

  @Column(name = "total_net", nullable = false)
  private BigDecimal totalNet;

  @Column(name = "total_vat", nullable = false)
  private BigDecimal totalVat;

  @Column(name = "total_gross", nullable = false)
  private BigDecimal totalGross;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 32)
  private InvoiceStatus status;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 16)
  private InvoiceDocumentType type = InvoiceDocumentType.EINVOICE;

  // Internet Sales
  @Column(name = "is_online_sale")
  private Boolean isOnlineSale = false;

  @Column(name = "online_website", length = 512)
  private String onlineWebsite;

  @Column(name = "payment_method", length = 64)
  private String paymentMethod;

  @Column(name = "payment_platform", length = 128)
  private String paymentPlatform;

  @Column(name = "payment_date")
  private LocalDate paymentDate;

  // Delivery Information
  @Enumerated(EnumType.STRING)
  @Column(name = "delivery_method", length = 16)
  private DeliveryMethod deliveryMethod = DeliveryMethod.NONE;

  // Cargo fields
  @Column(name = "cargo_company", length = 255)
  private String cargoCompany;

  @Column(name = "cargo_tax_number", length = 32)
  private String cargoTaxNumber;

  @Column(name = "cargo_date")
  private LocalDate cargoDate;

  // Courier fields
  @Column(name = "courier_tc", length = 11)
  private String courierTc;

  @Column(name = "courier_name", length = 100)
  private String courierName;

  @Column(name = "courier_surname", length = 100)
  private String courierSurname;

  @Column(name = "delivery_date")
  private LocalDate deliveryDate;

  // Provider/Sending Information
  @Column(name = "sent_at")
  private OffsetDateTime sentAt;

  @Column(name = "provider_document_uuid", length = 255)
  private String providerDocumentUuid;

  @Column(name = "provider_response_code", length = 16)
  private String providerResponseCode;

  @Column(name = "provider_response_explanation", length = 1024)
  private String providerResponseExplanation;

  @Column(name = "destination_urn", length = 512)
  private String destinationUrn;

  // Cancellation Information
  @Column(name = "canceled_at")
  private OffsetDateTime canceledAt;

  @Column(name = "cancel_reason", length = 512)
  private String cancelReason;

  @Column(name = "cancel_date", length = 32)
  private String cancelDate;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "invoice_id")
  private List<InvoiceLine> lines = new ArrayList<>();

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  @Override
  public UUID getTenantId() {
    return tenantId;
  }

  @Override
  public void setTenantId(UUID tenantId) {
    this.tenantId = tenantId;
  }

  public String getInvoiceNo() {
    return invoiceNo;
  }

  public void setInvoiceNo(String invoiceNo) {
    this.invoiceNo = invoiceNo;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    this.customerId = customerId;
  }

  public LocalDate getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(LocalDate issueDate) {
    this.issueDate = issueDate;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BigDecimal getTotalNet() {
    return totalNet;
  }

  public void setTotalNet(BigDecimal totalNet) {
    this.totalNet = totalNet;
  }

  public BigDecimal getTotalVat() {
    return totalVat;
  }

  public void setTotalVat(BigDecimal totalVat) {
    this.totalVat = totalVat;
  }

  public BigDecimal getTotalGross() {
    return totalGross;
  }

  public void setTotalGross(BigDecimal totalGross) {
    this.totalGross = totalGross;
  }

  public InvoiceStatus getStatus() {
    return status;
  }

  public void setStatus(InvoiceStatus status) {
    this.status = status;
  }

  public InvoiceDocumentType getType() {
    return type;
  }

  public void setType(InvoiceDocumentType type) {
    this.type = type;
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

  public List<InvoiceLine> getLines() {
    return lines;
  }

  public void setLines(List<InvoiceLine> lines) {
    this.lines = lines;
  }

  public Boolean getIsOnlineSale() {
    return isOnlineSale;
  }

  public void setIsOnlineSale(Boolean isOnlineSale) {
    this.isOnlineSale = isOnlineSale;
  }

  public String getOnlineWebsite() {
    return onlineWebsite;
  }

  public void setOnlineWebsite(String onlineWebsite) {
    this.onlineWebsite = onlineWebsite;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public String getPaymentPlatform() {
    return paymentPlatform;
  }

  public void setPaymentPlatform(String paymentPlatform) {
    this.paymentPlatform = paymentPlatform;
  }

  public LocalDate getPaymentDate() {
    return paymentDate;
  }

  public void setPaymentDate(LocalDate paymentDate) {
    this.paymentDate = paymentDate;
  }

  public DeliveryMethod getDeliveryMethod() {
    return deliveryMethod;
  }

  public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
    this.deliveryMethod = deliveryMethod;
  }

  public String getCargoCompany() {
    return cargoCompany;
  }

  public void setCargoCompany(String cargoCompany) {
    this.cargoCompany = cargoCompany;
  }

  public String getCargoTaxNumber() {
    return cargoTaxNumber;
  }

  public void setCargoTaxNumber(String cargoTaxNumber) {
    this.cargoTaxNumber = cargoTaxNumber;
  }

  public LocalDate getCargoDate() {
    return cargoDate;
  }

  public void setCargoDate(LocalDate cargoDate) {
    this.cargoDate = cargoDate;
  }

  public String getCourierTc() {
    return courierTc;
  }

  public void setCourierTc(String courierTc) {
    this.courierTc = courierTc;
  }

  public String getCourierName() {
    return courierName;
  }

  public void setCourierName(String courierName) {
    this.courierName = courierName;
  }

  public String getCourierSurname() {
    return courierSurname;
  }

  public void setCourierSurname(String courierSurname) {
    this.courierSurname = courierSurname;
  }

  public LocalDate getDeliveryDate() {
    return deliveryDate;
  }

  public void setDeliveryDate(LocalDate deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  public OffsetDateTime getSentAt() {
    return sentAt;
  }

  public void setSentAt(OffsetDateTime sentAt) {
    this.sentAt = sentAt;
  }

  public String getProviderDocumentUuid() {
    return providerDocumentUuid;
  }

  public void setProviderDocumentUuid(String providerDocumentUuid) {
    this.providerDocumentUuid = providerDocumentUuid;
  }

  public String getProviderResponseCode() {
    return providerResponseCode;
  }

  public void setProviderResponseCode(String providerResponseCode) {
    this.providerResponseCode = providerResponseCode;
  }

  public String getProviderResponseExplanation() {
    return providerResponseExplanation;
  }

  public void setProviderResponseExplanation(String providerResponseExplanation) {
    this.providerResponseExplanation = providerResponseExplanation;
  }

  public String getDestinationUrn() {
    return destinationUrn;
  }

  public void setDestinationUrn(String destinationUrn) {
    this.destinationUrn = destinationUrn;
  }

  public OffsetDateTime getCanceledAt() {
    return canceledAt;
  }

  public void setCanceledAt(OffsetDateTime canceledAt) {
    this.canceledAt = canceledAt;
  }

  public String getCancelReason() {
    return cancelReason;
  }

  public void setCancelReason(String cancelReason) {
    this.cancelReason = cancelReason;
  }

  public String getCancelDate() {
    return cancelDate;
  }

  public void setCancelDate(String cancelDate) {
    this.cancelDate = cancelDate;
  }
}
