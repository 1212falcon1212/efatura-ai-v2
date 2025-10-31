package com.efaturaai.aiinsights;

import com.efaturaai.aiops.ClassificationResult;
import com.efaturaai.aiops.ErrorClassifierService;
import com.efaturaai.core.domain.AiClassification;
import com.efaturaai.core.domain.Invoice;
import com.efaturaai.core.repository.AiClassificationRepository;
import com.efaturaai.core.repository.InvoiceRepository;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InsightsService {
  private final AiClassificationRepository classificationRepository;
  private final InvoiceRepository invoiceRepository;
  private final ErrorClassifierService errorClassifier;

  public InsightsService(
      AiClassificationRepository classificationRepository,
      InvoiceRepository invoiceRepository,
      ErrorClassifierService errorClassifier) {
    this.classificationRepository = classificationRepository;
    this.invoiceRepository = invoiceRepository;
    this.errorClassifier = errorClassifier;
  }

  public Page<InsightCard> getInsights(UUID tenantId, Pageable pageable) {
    Page<AiClassification> classifications = classificationRepository.findByTenantId(tenantId, pageable);
    return classifications.map(this::toInsightCard);
  }

  public InsightSummary getSummary(UUID tenantId) {
    OffsetDateTime last24Hours = OffsetDateTime.now().minusHours(24);
    List<AiClassification> recent = classificationRepository.findByTenantId(tenantId, 
        org.springframework.data.domain.PageRequest.of(0, 1000))
        .stream()
        .filter(c -> c.getCreatedAt().isAfter(last24Hours))
        .toList();

    long totalErrors = recent.size();
    long retryRecommended = recent.stream()
        .filter(c -> c.getSuggestedAction().equals("RETRY"))
        .count();
    
    double retrySuccessRate = totalErrors > 0 ? (double) retryRecommended / totalErrors : 0.0;

    return new InsightSummary(
        totalErrors,
        retryRecommended,
        retrySuccessRate,
        calculateErrorDistribution(recent)
    );
  }

  public InsightCard generateInsightForFailedInvoice(UUID invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
        .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

    String errorMessage = invoice.getProviderResponseExplanation() != null 
        ? invoice.getProviderResponseExplanation() 
        : "Invoice send failed";
    String errorCode = invoice.getProviderResponseCode();

    ClassificationResult result = errorClassifier.classify(
        errorMessage,
        errorCode,
        "{\"invoiceId\":\"" + invoiceId + "\"}",
        invoice.getTenantId(),
        "invoice.send"
    );

    return new InsightCard(
        invoiceId,
        invoice.getInvoiceNo(),
        result.errorType().name(),
        result.confidence(),
        result.suggestedAction().name(),
        "Invoice send failed: " + errorMessage,
        result.suggestedAction().name().equals("RETRY") ? "Retry sending invoice" : "Review invoice details"
    );
  }

  private InsightCard toInsightCard(AiClassification classification) {
    return new InsightCard(
        null, // Invoice ID not available in classification
        null,
        classification.getErrorType(),
        classification.getConfidence(),
        classification.getSuggestedAction(),
        "Classification: " + classification.getErrorType(),
        "Suggested action: " + classification.getSuggestedAction()
    );
  }

  private List<ErrorDistribution> calculateErrorDistribution(List<AiClassification> classifications) {
    List<ErrorDistribution> distribution = new ArrayList<>();
    classifications.stream()
        .collect(java.util.stream.Collectors.groupingBy(
            AiClassification::getErrorType,
            java.util.stream.Collectors.counting()))
        .forEach((errorType, count) -> {
          distribution.add(new ErrorDistribution(errorType, count.intValue()));
        });
    return distribution;
  }
}
