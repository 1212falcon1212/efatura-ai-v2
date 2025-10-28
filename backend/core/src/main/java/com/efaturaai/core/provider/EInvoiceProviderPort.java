package com.efaturaai.core.provider;

public interface EInvoiceProviderPort {
  ProviderSendResponse sendInvoice(String ublXml);

  ProviderStatusResponse getInvoiceStatus(String invoiceUuid);

  ProviderCancelResponse cancelInvoice(String invoiceUuid);
}
