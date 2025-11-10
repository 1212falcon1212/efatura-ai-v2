package com.efaturaai.core.provider;

public class InputDocument {
  public String documentUUID;
  public String xmlContent;
  public String sourceUrn;
  public String destinationUrn;
  public String documentNoPrefix;
  public String localId;
  public String documentId;
  public Boolean submitForApproval;
  public String documentDate; // ISO string as WSDL expects string
  public String note;
}


