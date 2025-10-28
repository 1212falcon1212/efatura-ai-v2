package com.efaturaai.signer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "signer.pkcs11")
public class SignerProperties {
  private String libraryPath;
  private String slot;
  private String pin;

  public String getLibraryPath() {
    return libraryPath;
  }

  public void setLibraryPath(String libraryPath) {
    this.libraryPath = libraryPath;
  }

  public String getSlot() {
    return slot;
  }

  public void setSlot(String slot) {
    this.slot = slot;
  }

  public String getPin() {
    return pin;
  }

  public void setPin(String pin) {
    this.pin = pin;
  }
}
