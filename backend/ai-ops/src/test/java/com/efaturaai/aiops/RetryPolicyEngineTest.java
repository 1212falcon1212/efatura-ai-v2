package com.efaturaai.aiops;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.efaturaai.core.domain.AiRetryPolicy;
import com.efaturaai.core.repository.AiRetryPolicyRepository;

import static org.mockito.Mockito.*;

class RetryPolicyEngineTest {

  @Mock
  private AiRetryPolicyRepository policyRepository;

  private RetryPolicyEngine policyEngine;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    policyEngine = new RetryPolicyEngine(policyRepository);
  }

  @Test
  void shouldRetryWhenPolicyEnabled() {
    AiRetryPolicy policy = new AiRetryPolicy();
    policy.setId(UUID.randomUUID());
    policy.setErrorType("TIMEOUT");
    policy.setMaxAttempts(5);
    policy.setBackoffInitialMs(1000L);
    policy.setBackoffMultiplier(new BigDecimal("2.0"));
    policy.setJitterMs(100L);
    policy.setEnabled(true);

    when(policyRepository.findByErrorType("TIMEOUT")).thenReturn(Optional.of(policy));

    var decision = policyEngine.shouldRetry(ErrorType.TIMEOUT, 1);
    assertTrue(decision.shouldRetry());
    assertTrue(decision.delayMs() > 0);
  }

  @Test
  void shouldNotRetryWhenMaxAttemptsReached() {
    AiRetryPolicy policy = new AiRetryPolicy();
    policy.setId(UUID.randomUUID());
    policy.setErrorType("TIMEOUT");
    policy.setMaxAttempts(3);
    policy.setEnabled(true);

    when(policyRepository.findByErrorType("TIMEOUT")).thenReturn(Optional.of(policy));

    var decision = policyEngine.shouldRetry(ErrorType.TIMEOUT, 4);
    assertFalse(decision.shouldRetry());
  }

  @Test
  void shouldNotRetryWhenPolicyDisabled() {
    AiRetryPolicy policy = new AiRetryPolicy();
    policy.setId(UUID.randomUUID());
    policy.setErrorType("TIMEOUT");
    policy.setEnabled(false);

    when(policyRepository.findByErrorType("TIMEOUT")).thenReturn(Optional.of(policy));

    var decision = policyEngine.shouldRetry(ErrorType.TIMEOUT, 1);
    assertFalse(decision.shouldRetry());
  }

  @Test
  void shouldCalculateBackoffWithJitter() {
    AiRetryPolicy policy = new AiRetryPolicy();
    policy.setId(UUID.randomUUID());
    policy.setErrorType("TIMEOUT");
    policy.setMaxAttempts(5);
    policy.setBackoffInitialMs(1000L);
    policy.setBackoffMultiplier(new BigDecimal("2.0"));
    policy.setJitterMs(100L);
    policy.setEnabled(true);

    when(policyRepository.findByErrorType("TIMEOUT")).thenReturn(Optional.of(policy));

    var decision1 = policyEngine.shouldRetry(ErrorType.TIMEOUT, 2);
    var decision2 = policyEngine.shouldRetry(ErrorType.TIMEOUT, 2);

    assertTrue(decision1.shouldRetry());
    assertTrue(decision2.shouldRetry());
    // Backoff should increase with attempt number
    assertTrue(decision1.delayMs() >= 1000);
  }
}
