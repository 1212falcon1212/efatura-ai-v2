package com.efaturaai.aicore;

import com.efaturaai.core.domain.AiFeaturesInvoice;
import com.efaturaai.core.domain.Invoice;
import com.efaturaai.core.repository.AiFeaturesInvoiceRepository;
import com.efaturaai.core.repository.InvoiceRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.DistributionSummary;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeatureExtractorService {
  private static final Logger log = LoggerFactory.getLogger(FeatureExtractorService.class);

  private final AiFeaturesInvoiceRepository featuresRepository;
  private final InvoiceRepository invoiceRepository;
  private final MeterRegistry meterRegistry;

  public FeatureExtractorService(
      AiFeaturesInvoiceRepository featuresRepository,
      InvoiceRepository invoiceRepository,
      MeterRegistry meterRegistry) {
    this.featuresRepository = featuresRepository;
    this.invoiceRepository = invoiceRepository;
    this.meterRegistry = meterRegistry;
  }

  @Transactional
  public AiFeaturesInvoice extractFeatures(UUID invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
        .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

    // Check if features already exist
    return featuresRepository.findByInvoiceId(invoiceId)
        .orElseGet(() -> {
          AiFeaturesInvoice features = new AiFeaturesInvoice();
          features.setId(UUID.randomUUID());
          features.setInvoiceId(invoiceId);
          features.setTenantId(invoice.getTenantId());
          features.setItemCount(invoice.getLines() != null ? invoice.getLines().size() : 0);
          features.setTotalAmount(invoice.getTotalGross());
          features.setPreviousFailRate(calculatePreviousFailRate(invoice.getTenantId()));
          features.setEndpointLatencyMs(getEndpointLatency());
          features.setHourOfDay(OffsetDateTime.now().getHour());
          features.setCreatedAt(OffsetDateTime.now());
          
          AiFeaturesInvoice saved = featuresRepository.save(features);
          log.debug("Features extracted for invoiceId={}, itemCount={}, totalAmount={}", 
              invoiceId, features.getItemCount(), features.getTotalAmount());
          return saved;
        });
  }

  private BigDecimal calculatePreviousFailRate(UUID tenantId) {
    // Calculate failure rate from last invoices
    long totalInvoices = invoiceRepository.countByTenantId(tenantId);
    if (totalInvoices == 0) {
      return BigDecimal.ZERO;
    }
    
    // Simple heuristic: count failed invoices (status = FAILED)
    // In production, query actual failed invoices
    return BigDecimal.valueOf(0.05); // Placeholder: 5% failure rate
  }

  private Long getEndpointLatency() {
    // Get average latency from Prometheus metrics
    try {
      DistributionSummary summary = meterRegistry.find("http.server.requests")
          .tag("uri", "/api/invoices")
          .summary();
      if (summary != null) {
        return (long) summary.mean();
      }
      return 100L;
    } catch (Exception e) {
      log.warn("Could not get endpoint latency from metrics", e);
      return 100L; // Default latency
    }
  }
}
