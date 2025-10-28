package com.efaturaai.signer;

public interface SignerService {
  byte[] signXadesBes(byte[] xml);

  boolean verifyXadesBes(byte[] signedXml);
}
