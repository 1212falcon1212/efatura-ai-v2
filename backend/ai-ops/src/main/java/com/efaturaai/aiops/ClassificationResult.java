package com.efaturaai.aiops;

import java.math.BigDecimal;

public record ClassificationResult(
    ErrorType errorType,
    BigDecimal confidence,
    SuggestedAction suggestedAction
) {
  public ClassificationResult {
    if (confidence.compareTo(BigDecimal.ZERO) < 0 || confidence.compareTo(BigDecimal.ONE) > 0) {
      throw new IllegalArgumentException("Confidence must be between 0 and 1");
    }
  }
}
