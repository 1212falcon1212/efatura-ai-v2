package com.efaturaai.core.provider;

import com.efaturaai.core.provider.query.DocumentQueryResponse;

public interface ProviderEArchiveQueryPort {
  String queryInvoicesWithDocumentDate(String startDate, String endDate, String withXML, String minRecordId);

  DocumentQueryResponse queryInvoicesWithDocumentDateDto(String startDate, String endDate, String withXML, String minRecordId);
}


