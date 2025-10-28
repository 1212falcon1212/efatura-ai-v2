package com.efaturaai.signer;

import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"default", "test", "it"})
public class MockSignerService implements SignerService {
  @Override
  public byte[] signXadesBes(byte[] xml) {
    // Simple mock: wrap with <Signed> for tests
    String content = new String(xml, StandardCharsets.UTF_8);
    return ("<Signed>" + content + "</Signed>").getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public boolean verifyXadesBes(byte[] signedXml) {
    String s = new String(signedXml, StandardCharsets.UTF_8);
    return s.startsWith("<Signed>") && s.endsWith("</Signed>");
  }
}
