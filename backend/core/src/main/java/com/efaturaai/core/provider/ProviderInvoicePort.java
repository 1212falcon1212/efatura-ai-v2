package com.efaturaai.core.provider;

import java.util.List;

public interface ProviderInvoicePort {
  List<EntResponse> sendInvoice(List<InputDocument> inputDocuments);

  List<EntResponse> updateInvoice(List<InputDocument> inputDocuments);

  EntResponse cancelInvoice(String invoiceUuid, String cancelReason, String cancelDate);
}


