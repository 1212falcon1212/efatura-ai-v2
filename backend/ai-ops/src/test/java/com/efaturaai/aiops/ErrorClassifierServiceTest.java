package com.efaturaai.aiops;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.efaturaai.core.repository.AiErrorSampleRepository;

class ErrorClassifierServiceTest {

  @Mock
  private AiErrorSampleRepository errorSampleRepository;

  private ErrorClassifierService classifierService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    classifierService = new ErrorClassifierService(errorSampleRepository);
  }

  @Test
  void shouldClassifyTimeout() {
    UUID tenantId = UUID.randomUUID();
    var result = classifierService.classify(
        "Connection timeout after 30 seconds",
        null,
        "{}",
        tenantId,
        "test"
    );
    assertEquals(ErrorType.TIMEOUT, result.errorType());
    assertTrue(result.confidence().compareTo(new BigDecimal("0.80")) >= 0);
    assertEquals(SuggestedAction.RETRY, result.suggestedAction());
  }

  @Test
  void shouldClassifySoapFault() {
    UUID tenantId = UUID.randomUUID();
    var result = classifierService.classify(
        "SOAP fault: Invalid request",
        "SOAP_FAULT",
        "{}",
        tenantId,
        "test"
    );
    assertEquals(ErrorType.SOAP_FAULT, result.errorType());
    assertTrue(result.confidence().compareTo(new BigDecimal("0.75")) >= 0);
  }

  @Test
  void shouldClassifyInvalidUbl() {
    UUID tenantId = UUID.randomUUID();
    var result = classifierService.classify(
        "UBL validation failed: schema error",
        "2002",
        "{}",
        tenantId,
        "test"
    );
    assertEquals(ErrorType.INVALID_UBL, result.errorType());
    assertTrue(result.confidence().compareTo(new BigDecimal("0.85")) >= 0);
    assertEquals(SuggestedAction.CHECK_UBL, result.suggestedAction());
  }

  @Test
  void shouldClassifySigningError() {
    UUID tenantId = UUID.randomUUID();
    var result = classifierService.classify(
        "HSM signing error: certificate not found",
        null,
        "{}",
        tenantId,
        "test"
    );
    assertEquals(ErrorType.SIGNING_ERROR, result.errorType());
    assertEquals(SuggestedAction.CHECK_HSM, result.suggestedAction());
  }

  @Test
  void shouldClassifyRateLimit() {
    UUID tenantId = UUID.randomUUID();
    var result = classifierService.classify(
        "Rate limit exceeded: too many requests",
        "429",
        "{}",
        tenantId,
        "test"
    );
    assertEquals(ErrorType.RATE_LIMIT, result.errorType());
    assertTrue(result.confidence().compareTo(new BigDecimal("0.90")) >= 0);
    assertEquals(SuggestedAction.WAIT, result.suggestedAction());
  }

  @Test
  void shouldClassifyAuth() {
    UUID tenantId = UUID.randomUUID();
    var result = classifierService.classify(
        "Authentication failed: invalid credentials",
        "1001",
        "{}",
        tenantId,
        "test"
    );
    assertEquals(ErrorType.AUTH, result.errorType());
    assertTrue(result.confidence().compareTo(new BigDecimal("0.90")) >= 0);
    assertEquals(SuggestedAction.CHECK_HSM, result.suggestedAction());
  }

  @Test
  void shouldClassifyNetwork() {
    UUID tenantId = UUID.randomUUID();
    var result = classifierService.classify(
        "Network error: connection refused",
        null,
        "{}",
        tenantId,
        "test"
    );
    assertEquals(ErrorType.NETWORK, result.errorType());
    assertEquals(SuggestedAction.RETRY, result.suggestedAction());
  }

  @Test
  void shouldClassifyOther() {
    UUID tenantId = UUID.randomUUID();
    var result = classifierService.classify(
        "Unknown error occurred",
        null,
        "{}",
        tenantId,
        "test"
    );
    assertEquals(ErrorType.OTHER, result.errorType());
    assertTrue(result.confidence().compareTo(new BigDecimal("0.50")) >= 0);
    assertEquals(SuggestedAction.CONTACT_SUPPORT, result.suggestedAction());
  }

  @Test
  void shouldHaveValidConfidence() {
    UUID tenantId = UUID.randomUUID();
    var result = classifierService.classify(
        "Test error",
        null,
        "{}",
        tenantId,
        "test"
    );
    assertTrue(result.confidence().compareTo(BigDecimal.ZERO) >= 0);
    assertTrue(result.confidence().compareTo(BigDecimal.ONE) <= 0);
  }
}
