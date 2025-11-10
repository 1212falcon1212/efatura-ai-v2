package com.efaturaai.core.domain;

import com.efaturaai.core.tenant.TenantAware;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer implements TenantAware {
  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "short_name", length = 100)
  private String shortName;

  @Column(name = "tax_number", length = 32)
  private String taxNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "customer_type", nullable = false, length = 16)
  private CustomerType customerType = CustomerType.LEGAL_ENTITY;

  @Column(name = "email", length = 255)
  private String email;

  @Column(name = "phone", length = 32)
  private String phone;

  @Column(name = "address", length = 1024)
  private String address;

  @Column(name = "postal_code", length = 10)
  private String postalCode;

  @Column(name = "district", length = 100)
  private String district;

  @Column(name = "city", length = 100)
  private String city;

  @Column(name = "is_abroad", nullable = false)
  private Boolean isAbroad = false;

  @Column(name = "tax_office", length = 255)
  private String taxOffice;

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

  @Override
  public UUID getTenantId() {
    return tenantId;
  }

  @Override
  public void setTenantId(UUID tenantId) {
    this.tenantId = tenantId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getTaxNumber() {
    return taxNumber;
  }

  public void setTaxNumber(String taxNumber) {
    this.taxNumber = taxNumber;
  }

  public CustomerType getCustomerType() {
    return customerType;
  }

  public void setCustomerType(CustomerType customerType) {
    this.customerType = customerType;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getDistrict() {
    return district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Boolean getIsAbroad() {
    return isAbroad;
  }

  public void setIsAbroad(Boolean isAbroad) {
    this.isAbroad = isAbroad;
  }

  public String getTaxOffice() {
    return taxOffice;
  }

  public void setTaxOffice(String taxOffice) {
    this.taxOffice = taxOffice;
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
