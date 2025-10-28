package com.efaturaai.core.provider;

public class ProviderStatusResponse {
  private String invoiceUuid;
  private String status;
  private String message;

  public String getInvoiceUuid() {
    return invoiceUuid;
  }

  public void setInvoiceUuid(String invoiceUuid) {
    this.invoiceUuid = invoiceUuid;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
