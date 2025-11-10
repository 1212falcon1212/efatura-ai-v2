package com.efaturaai.aiops;

public enum ErrorType {
  TIMEOUT,
  SOAP_FAULT,
  INVALID_UBL,
  SIGNING_ERROR,
  RATE_LIMIT,
  AUTH,
  NETWORK,
  OTHER
}
