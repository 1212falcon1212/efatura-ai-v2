package com.efaturaai.aiinsights;

import java.util.List;

public record InsightSummary(
    long totalErrors,
    long retryRecommended,
    double retrySuccessRate,
    List<ErrorDistribution> errorDistribution
) {}

