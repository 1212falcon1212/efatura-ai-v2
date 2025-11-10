package com.efaturaai.core.provider;

import com.efaturaai.core.provider.query.DocumentQueryResponse;
public interface ProviderQueryPort {
  /** Sends a raw XML request and returns raw XML response as string. */
  String send(String requestXml);

  /** Queries e-Invoice registered users between dates (optional vkn_tckn). Returns raw XML. */
  String queryUsers(String startDate, String finishDate, String vknTckn);

  /**
   * Queries outbox documents by document date with optional filters.
   * documentType: {"1" (Invoice), "2" (Application response)}
   * queried: {"YES","NO","ALL"}
   * withXML: {"XML","PDF","HTML","NONE"}
   */
  String queryOutboxDocumentsWithDocumentDate(
      String startDate, String endDate, String documentType, String queried, String withXML, String minRecordId);

  /**
   * Queries inbox documents by document date with optional filters.
   * documentType: {"1" (Invoice), "2" (Application response)}
   * queried: {"YES","NO","ALL"}
   * withXML: {"XML","PDF","HTML","NONE"}
   * takenFromEntegrator: {"YES","NO","ALL"}
   */
  String queryInboxDocumentsWithDocumentDate(
      String startDate,
      String endDate,
      String documentType,
      String queried,
      String withXML,
      String takenFromEntegrator,
      String minRecordId);

  DocumentQueryResponse queryOutboxDocumentsWithDocumentDateDto(
      String startDate, String endDate, String documentType, String queried, String withXML, String minRecordId);

  DocumentQueryResponse queryInboxDocumentsWithDocumentDateDto(
      String startDate,
      String endDate,
      String documentType,
      String queried,
      String withXML,
      String takenFromEntegrator,
      String minRecordId);
}


