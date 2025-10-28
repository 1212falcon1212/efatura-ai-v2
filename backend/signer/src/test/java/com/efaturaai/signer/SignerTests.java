package com.efaturaai.signer;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class SignerTests {

  @Test
  void sign_and_verify_mock() {
    MockSignerService signer = new MockSignerService();
    byte[] xml = "<Invoice/>".getBytes(StandardCharsets.UTF_8);
    byte[] signed = signer.signXadesBes(xml);
    assertTrue(signer.verifyXadesBes(signed));
  }
}
