package com.efaturaai.core.provider.query;

import java.util.ArrayList;
import java.util.List;

public class DocumentQueryResponse {
  private int queryState;
  private String stateExplanation;
  private int documentsCount;
  private int maxRecordIdinList;
  private List<ResponseDocument> documents = new ArrayList<>();

  public int getQueryState() { return queryState; }
  public void setQueryState(int queryState) { this.queryState = queryState; }
  public String getStateExplanation() { return stateExplanation; }
  public void setStateExplanation(String stateExplanation) { this.stateExplanation = stateExplanation; }
  public int getDocumentsCount() { return documentsCount; }
  public void setDocumentsCount(int documentsCount) { this.documentsCount = documentsCount; }
  public int getMaxRecordIdinList() { return maxRecordIdinList; }
  public void setMaxRecordIdinList(int maxRecordIdinList) { this.maxRecordIdinList = maxRecordIdinList; }
  public List<ResponseDocument> getDocuments() { return documents; }
  public void setDocuments(List<ResponseDocument> documents) { this.documents = documents; }
}


