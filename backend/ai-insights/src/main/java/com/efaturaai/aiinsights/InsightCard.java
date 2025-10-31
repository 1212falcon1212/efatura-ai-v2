package com.efaturaai.aiinsights;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record InsightCard(
    UUID invoiceId,
    String invoiceNo,
    String errorType,
    BigDecimal confidence,
    String suggestedAction,
    String reason,
    String actionText
) {}

