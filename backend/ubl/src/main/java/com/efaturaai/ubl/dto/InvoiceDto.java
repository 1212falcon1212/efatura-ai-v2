package com.efaturaai.ubl.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceDto {
  private String id;
  private String profileId = "EARSIVFATURA";
  private LocalDate issueDate;
  private String supplierName;
  private String customerName;
  private String currency = "TRY";
  private BigDecimal lineExtensionAmount;
  private BigDecimal taxAmount;
  private BigDecimal payableAmount;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getProfileId() {
    return profileId;
  }

  public void setProfileId(String profileId) {
    this.profileId = profileId;
  }

  public LocalDate getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(LocalDate issueDate) {
    this.issueDate = issueDate;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BigDecimal getLineExtensionAmount() {
    return lineExtensionAmount;
  }

  public void setLineExtensionAmount(BigDecimal lineExtensionAmount) {
    this.lineExtensionAmount = lineExtensionAmount;
  }

  public BigDecimal getTaxAmount() {
    return taxAmount;
  }

  public void setTaxAmount(BigDecimal taxAmount) {
    this.taxAmount = taxAmount;
  }

  public BigDecimal getPayableAmount() {
    return payableAmount;
  }

  public void setPayableAmount(BigDecimal payableAmount) {
    this.payableAmount = payableAmount;
  }
}
