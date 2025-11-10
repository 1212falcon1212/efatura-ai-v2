package com.efaturaai.aiops;

import com.efaturaai.core.domain.AiRetryPolicy;
import com.efaturaai.core.repository.AiRetryPolicyRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RetryPolicyEngine {
  private static final Logger log = LoggerFactory.getLogger(RetryPolicyEngine.class);

  private final AiRetryPolicyRepository policyRepository;
  private final Cache<String, AiRetryPolicy> policyCache;

  public RetryPolicyEngine(AiRetryPolicyRepository policyRepository) {
    this.policyRepository = policyRepository;
    this.policyCache = Caffeine.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(30, TimeUnit.MINUTES)
        .build();
  }

  public RetryDecision shouldRetry(ErrorType errorType, int currentAttempt) {
    AiRetryPolicy policy = getPolicy(errorType);
    if (policy == null || !policy.getEnabled()) {
      log.debug("Retry disabled for errorType={}, attempt={}", errorType, currentAttempt);
      return RetryDecision.noRetry();
    }

    if (currentAttempt >= policy.getMaxAttempts()) {
      log.debug("Max attempts reached for errorType={}, attempt={}", errorType, currentAttempt);
      return RetryDecision.noRetry();
    }

    long delayMs = calculateBackoff(currentAttempt, policy);
    log.debug("Retry decision: errorType={}, attempt={}, delayMs={}", errorType, currentAttempt, delayMs);
    return RetryDecision.retry(delayMs);
  }

  private AiRetryPolicy getPolicy(ErrorType errorType) {
    return policyCache.get(errorType.name(), key -> {
      Optional<AiRetryPolicy> policy = policyRepository.findByErrorType(key);
      return policy.orElse(null);
    });
  }

  private long calculateBackoff(int attempt, AiRetryPolicy policy) {
    BigDecimal multiplier = policy.getBackoffMultiplier();
    BigDecimal baseDelay = BigDecimal.valueOf(policy.getBackoffInitialMs());
    BigDecimal delay = baseDelay.multiply(multiplier.pow(attempt - 1));
    
    // Add jitter
    long jitter = (long) (Math.random() * policy.getJitterMs());
    return delay.longValue() + jitter;
  }

  public record RetryDecision(boolean shouldRetry, long delayMs) {
    public static RetryDecision retry(long delayMs) {
      return new RetryDecision(true, delayMs);
    }

    public static RetryDecision noRetry() {
      return new RetryDecision(false, 0);
    }
  }
}
