package com.efaturaai.core.provider;

import java.math.BigDecimal;

public class CreditInfo {
  private String code;
  private String explanation;
  private BigDecimal totalCredit;
  private BigDecimal remainCredit;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

  public BigDecimal getTotalCredit() {
    return totalCredit;
  }

  public void setTotalCredit(BigDecimal totalCredit) {
    this.totalCredit = totalCredit;
  }

  public BigDecimal getRemainCredit() {
    return remainCredit;
  }

  public void setRemainCredit(BigDecimal remainCredit) {
    this.remainCredit = remainCredit;
  }
}


