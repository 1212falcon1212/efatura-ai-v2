package com.efaturaai.core.provider.query;

public class ResponseDocument {
  private String documentUuid;
  private String documentId;
  private String envelopeUuid;
  private String documentProfile;
  private String systemCreationTime;
  private String documentIssueDate;
  private String sourceId;
  private String destinationId;
  private String sourceUrn;
  private String sourceTitle;
  private String destinationUrn;
  private String currencyCode;
  private String invoiceTotal;
  private String stateCode;
  private String stateExplanation;
  private String cause;
  private String contentType;
  private byte[] content; // decoded from base64 of document_content
  private Integer emailSent;
  private String emailSentDate;
  private String cancelled;
  private String cancelDate;
  private String referenceDocumentUuid;
  private String responseDocumentUuid;
  private String responseCode;
  private String responseValidationState;
  private String responseReceivedDate;
  private String gtbReferenceNo;
  private String gtbGcbTescilNo;
  private String gtbFiiliIhracatTarihi;
  private String reserved1;
  private String reserved2;
  private String reserved3;
  private String documentTypeCode;
  private java.util.List<String> notes;
  private String taxInclusiveAmount;
  private String taxExlusiveAmount;
  private String allowanceTotalAmount;
  private String taxAmount0015;
  private String lineExtensionAmount;
  private String suplierPersonName;
  private String supplierPersonMiddleName;
  private String supplierPersonFamilyName;
  private String customerPersonName;
  private String customerPersonMiddleName;
  private String customerPersonFamilyName;
  private String destinationTitle;
  private String isRead;
  private String isArchieved;
  private String isAccounted;
  private String isTransferred;
  private String isPrinted;
  private String localId;
  private String sendingType;
  private String buyerCustomerPartyName;
  private String buyerCustomerPersonName;
  private String buyerCustomerPersonFamilyName;
  private String reportNo;
  private String cancelReportNo;
  private String objected;
  private String objectionReason;
  private String objectionDate;
  private String objectionReportNo;
  private String objectionType;
  private String objectionDocumentNo;
  private String destinationEmail;
  private String destinationMobile;
  private String smsSentDate;
  private Integer cancelPortalStatus;
  private String cancelReason;
  private String chargeTotalAmount;

  public String getDocumentUuid() { return documentUuid; }
  public void setDocumentUuid(String documentUuid) { this.documentUuid = documentUuid; }
  public String getDocumentId() { return documentId; }
  public void setDocumentId(String documentId) { this.documentId = documentId; }
  public String getEnvelopeUuid() { return envelopeUuid; }
  public void setEnvelopeUuid(String envelopeUuid) { this.envelopeUuid = envelopeUuid; }
  public String getDocumentProfile() { return documentProfile; }
  public void setDocumentProfile(String documentProfile) { this.documentProfile = documentProfile; }
  public String getSystemCreationTime() { return systemCreationTime; }
  public void setSystemCreationTime(String systemCreationTime) { this.systemCreationTime = systemCreationTime; }
  public String getDocumentIssueDate() { return documentIssueDate; }
  public void setDocumentIssueDate(String documentIssueDate) { this.documentIssueDate = documentIssueDate; }
  public String getSourceId() { return sourceId; }
  public void setSourceId(String sourceId) { this.sourceId = sourceId; }
  public String getDestinationId() { return destinationId; }
  public void setDestinationId(String destinationId) { this.destinationId = destinationId; }
  public String getSourceUrn() { return sourceUrn; }
  public void setSourceUrn(String sourceUrn) { this.sourceUrn = sourceUrn; }
  public String getSourceTitle() { return sourceTitle; }
  public void setSourceTitle(String sourceTitle) { this.sourceTitle = sourceTitle; }
  public String getDestinationUrn() { return destinationUrn; }
  public void setDestinationUrn(String destinationUrn) { this.destinationUrn = destinationUrn; }
  public String getCurrencyCode() { return currencyCode; }
  public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
  public String getInvoiceTotal() { return invoiceTotal; }
  public void setInvoiceTotal(String invoiceTotal) { this.invoiceTotal = invoiceTotal; }
  public String getStateCode() { return stateCode; }
  public void setStateCode(String stateCode) { this.stateCode = stateCode; }
  public String getStateExplanation() { return stateExplanation; }
  public void setStateExplanation(String stateExplanation) { this.stateExplanation = stateExplanation; }
  public String getCause() { return cause; }
  public void setCause(String cause) { this.cause = cause; }
  public String getContentType() { return contentType; }
  public void setContentType(String contentType) { this.contentType = contentType; }
  public byte[] getContent() { return content; }
  public void setContent(byte[] content) { this.content = content; }
  public Integer getEmailSent() { return emailSent; }
  public void setEmailSent(Integer emailSent) { this.emailSent = emailSent; }
  public String getEmailSentDate() { return emailSentDate; }
  public void setEmailSentDate(String emailSentDate) { this.emailSentDate = emailSentDate; }
  public String getCancelled() { return cancelled; }
  public void setCancelled(String cancelled) { this.cancelled = cancelled; }
  public String getCancelDate() { return cancelDate; }
  public void setCancelDate(String cancelDate) { this.cancelDate = cancelDate; }
  public String getReferenceDocumentUuid() { return referenceDocumentUuid; }
  public void setReferenceDocumentUuid(String referenceDocumentUuid) { this.referenceDocumentUuid = referenceDocumentUuid; }
  public String getResponseDocumentUuid() { return responseDocumentUuid; }
  public void setResponseDocumentUuid(String responseDocumentUuid) { this.responseDocumentUuid = responseDocumentUuid; }
  public String getResponseCode() { return responseCode; }
  public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
  public String getResponseValidationState() { return responseValidationState; }
  public void setResponseValidationState(String responseValidationState) { this.responseValidationState = responseValidationState; }
  public String getResponseReceivedDate() { return responseReceivedDate; }
  public void setResponseReceivedDate(String responseReceivedDate) { this.responseReceivedDate = responseReceivedDate; }
  public String getGtbReferenceNo() { return gtbReferenceNo; }
  public void setGtbReferenceNo(String gtbReferenceNo) { this.gtbReferenceNo = gtbReferenceNo; }
  public String getGtbGcbTescilNo() { return gtbGcbTescilNo; }
  public void setGtbGcbTescilNo(String gtbGcbTescilNo) { this.gtbGcbTescilNo = gtbGcbTescilNo; }
  public String getGtbFiiliIhracatTarihi() { return gtbFiiliIhracatTarihi; }
  public void setGtbFiiliIhracatTarihi(String gtbFiiliIhracatTarihi) { this.gtbFiiliIhracatTarihi = gtbFiiliIhracatTarihi; }
  public String getReserved1() { return reserved1; }
  public void setReserved1(String reserved1) { this.reserved1 = reserved1; }
  public String getReserved2() { return reserved2; }
  public void setReserved2(String reserved2) { this.reserved2 = reserved2; }
  public String getReserved3() { return reserved3; }
  public void setReserved3(String reserved3) { this.reserved3 = reserved3; }
  public String getDocumentTypeCode() { return documentTypeCode; }
  public void setDocumentTypeCode(String documentTypeCode) { this.documentTypeCode = documentTypeCode; }
  public java.util.List<String> getNotes() { return notes; }
  public void setNotes(java.util.List<String> notes) { this.notes = notes; }
  public String getTaxInclusiveAmount() { return taxInclusiveAmount; }
  public void setTaxInclusiveAmount(String taxInclusiveAmount) { this.taxInclusiveAmount = taxInclusiveAmount; }
  public String getTaxExlusiveAmount() { return taxExlusiveAmount; }
  public void setTaxExlusiveAmount(String taxExlusiveAmount) { this.taxExlusiveAmount = taxExlusiveAmount; }
  public String getAllowanceTotalAmount() { return allowanceTotalAmount; }
  public void setAllowanceTotalAmount(String allowanceTotalAmount) { this.allowanceTotalAmount = allowanceTotalAmount; }
  public String getTaxAmount0015() { return taxAmount0015; }
  public void setTaxAmount0015(String taxAmount0015) { this.taxAmount0015 = taxAmount0015; }
  public String getLineExtensionAmount() { return lineExtensionAmount; }
  public void setLineExtensionAmount(String lineExtensionAmount) { this.lineExtensionAmount = lineExtensionAmount; }
  public String getSuplierPersonName() { return suplierPersonName; }
  public void setSuplierPersonName(String suplierPersonName) { this.suplierPersonName = suplierPersonName; }
  public String getSupplierPersonMiddleName() { return supplierPersonMiddleName; }
  public void setSupplierPersonMiddleName(String supplierPersonMiddleName) { this.supplierPersonMiddleName = supplierPersonMiddleName; }
  public String getSupplierPersonFamilyName() { return supplierPersonFamilyName; }
  public void setSupplierPersonFamilyName(String supplierPersonFamilyName) { this.supplierPersonFamilyName = supplierPersonFamilyName; }
  public String getCustomerPersonName() { return customerPersonName; }
  public void setCustomerPersonName(String customerPersonName) { this.customerPersonName = customerPersonName; }
  public String getCustomerPersonMiddleName() { return customerPersonMiddleName; }
  public void setCustomerPersonMiddleName(String customerPersonMiddleName) { this.customerPersonMiddleName = customerPersonMiddleName; }
  public String getCustomerPersonFamilyName() { return customerPersonFamilyName; }
  public void setCustomerPersonFamilyName(String customerPersonFamilyName) { this.customerPersonFamilyName = customerPersonFamilyName; }
  public String getDestinationTitle() { return destinationTitle; }
  public void setDestinationTitle(String destinationTitle) { this.destinationTitle = destinationTitle; }
  public String getIsRead() { return isRead; }
  public void setIsRead(String isRead) { this.isRead = isRead; }
  public String getIsArchieved() { return isArchieved; }
  public void setIsArchieved(String isArchieved) { this.isArchieved = isArchieved; }
  public String getIsAccounted() { return isAccounted; }
  public void setIsAccounted(String isAccounted) { this.isAccounted = isAccounted; }
  public String getIsTransferred() { return isTransferred; }
  public void setIsTransferred(String isTransferred) { this.isTransferred = isTransferred; }
  public String getIsPrinted() { return isPrinted; }
  public void setIsPrinted(String isPrinted) { this.isPrinted = isPrinted; }
  public String getLocalId() { return localId; }
  public void setLocalId(String localId) { this.localId = localId; }
  public String getSendingType() { return sendingType; }
  public void setSendingType(String sendingType) { this.sendingType = sendingType; }
  public String getBuyerCustomerPartyName() { return buyerCustomerPartyName; }
  public void setBuyerCustomerPartyName(String buyerCustomerPartyName) { this.buyerCustomerPartyName = buyerCustomerPartyName; }
  public String getBuyerCustomerPersonName() { return buyerCustomerPersonName; }
  public void setBuyerCustomerPersonName(String buyerCustomerPersonName) { this.buyerCustomerPersonName = buyerCustomerPersonName; }
  public String getBuyerCustomerPersonFamilyName() { return buyerCustomerPersonFamilyName; }
  public void setBuyerCustomerPersonFamilyName(String buyerCustomerPersonFamilyName) { this.buyerCustomerPersonFamilyName = buyerCustomerPersonFamilyName; }
  public String getReportNo() { return reportNo; }
  public void setReportNo(String reportNo) { this.reportNo = reportNo; }
  public String getCancelReportNo() { return cancelReportNo; }
  public void setCancelReportNo(String cancelReportNo) { this.cancelReportNo = cancelReportNo; }
  public String getObjected() { return objected; }
  public void setObjected(String objected) { this.objected = objected; }
  public String getObjectionReason() { return objectionReason; }
  public void setObjectionReason(String objectionReason) { this.objectionReason = objectionReason; }
  public String getObjectionDate() { return objectionDate; }
  public void setObjectionDate(String objectionDate) { this.objectionDate = objectionDate; }
  public String getObjectionReportNo() { return objectionReportNo; }
  public void setObjectionReportNo(String objectionReportNo) { this.objectionReportNo = objectionReportNo; }
  public String getObjectionType() { return objectionType; }
  public void setObjectionType(String objectionType) { this.objectionType = objectionType; }
  public String getObjectionDocumentNo() { return objectionDocumentNo; }
  public void setObjectionDocumentNo(String objectionDocumentNo) { this.objectionDocumentNo = objectionDocumentNo; }
  public String getDestinationEmail() { return destinationEmail; }
  public void setDestinationEmail(String destinationEmail) { this.destinationEmail = destinationEmail; }
  public String getDestinationMobile() { return destinationMobile; }
  public void setDestinationMobile(String destinationMobile) { this.destinationMobile = destinationMobile; }
  public String getSmsSentDate() { return smsSentDate; }
  public void setSmsSentDate(String smsSentDate) { this.smsSentDate = smsSentDate; }
  public Integer getCancelPortalStatus() { return cancelPortalStatus; }
  public void setCancelPortalStatus(Integer cancelPortalStatus) { this.cancelPortalStatus = cancelPortalStatus; }
  public String getCancelReason() { return cancelReason; }
  public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
  public String getChargeTotalAmount() { return chargeTotalAmount; }
  public void setChargeTotalAmount(String chargeTotalAmount) { this.chargeTotalAmount = chargeTotalAmount; }
}


