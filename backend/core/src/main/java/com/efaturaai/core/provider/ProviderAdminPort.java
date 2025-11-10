package com.efaturaai.core.provider;

public interface ProviderAdminPort {
  CreditInfo getCustomerCreditCount(String vknTckn);

  /** Returns sender unit (GB) labels for the authenticated user. */
  java.util.List<String> getCustomerGBList();

  /** Returns mailbox (PK) labels for the authenticated user. */
  java.util.List<String> getCustomerPKList();
}


