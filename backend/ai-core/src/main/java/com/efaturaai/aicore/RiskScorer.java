package com.efaturaai.aicore;

import com.efaturaai.core.domain.AiFeaturesInvoice;
import com.efaturaai.core.domain.AiRiskScore;
import com.efaturaai.core.domain.Invoice;
import com.efaturaai.core.repository.AiRiskScoreRepository;
import com.efaturaai.core.repository.InvoiceRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RiskScorer {
  private static final Logger log = LoggerFactory.getLogger(RiskScorer.class);
  private static final int RISK_THRESHOLD = 65;

  private final AiRiskScoreRepository riskScoreRepository;
  private final InvoiceRepository invoiceRepository;
  private final FeatureExtractorService featureExtractor;

  public RiskScorer(
      AiRiskScoreRepository riskScoreRepository,
      InvoiceRepository invoiceRepository,
      FeatureExtractorService featureExtractor) {
    this.riskScoreRepository = riskScoreRepository;
    this.invoiceRepository = invoiceRepository;
    this.featureExtractor = featureExtractor;
  }

  @Transactional
  public int calculateRiskScore(UUID invoiceId) {
    Invoice invoice = invoiceRepository.findById(invoiceId)
        .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

    // Check if score already exists
    return riskScoreRepository.findByInvoiceId(invoiceId)
        .map(AiRiskScore::getScore0100)
        .orElseGet(() -> {
          AiFeaturesInvoice features = featureExtractor.extractFeatures(invoiceId);
          int score = computeScore(features);
          
          AiRiskScore riskScore = new AiRiskScore();
          riskScore.setId(UUID.randomUUID());
          riskScore.setInvoiceId(invoiceId);
          riskScore.setTenantId(invoice.getTenantId());
          riskScore.setScore0100(score);
          riskScore.setModelVersion("v1.0");
          riskScore.setCreatedAt(OffsetDateTime.now());
          riskScoreRepository.save(riskScore);
          
          log.info("Risk score calculated for invoiceId={}: {}", invoiceId, score);
          return score;
        });
  }

  private int computeScore(AiFeaturesInvoice features) {
    int score = 0;
    
    // High item count increases risk
    if (features.getItemCount() > 50) {
      score += 15;
    } else if (features.getItemCount() > 20) {
      score += 10;
    }
    
    // High amount increases risk
    if (features.getTotalAmount().compareTo(new BigDecimal("100000")) > 0) {
      score += 20;
    } else if (features.getTotalAmount().compareTo(new BigDecimal("50000")) > 0) {
      score += 10;
    }
    
    // Previous failure rate
    BigDecimal failRate = features.getPreviousFailRate();
    if (failRate.compareTo(new BigDecimal("0.20")) > 0) {
      score += 25;
    } else if (failRate.compareTo(new BigDecimal("0.10")) > 0) {
      score += 15;
    }
    
    // High latency
    if (features.getEndpointLatencyMs() != null && features.getEndpointLatencyMs() > 5000) {
      score += 10;
    }
    
    // Off-peak hours (higher risk)
    if (features.getHourOfDay() < 6 || features.getHourOfDay() > 22) {
      score += 5;
    }
    
    return Math.min(score, 100);
  }

  public boolean isHighRisk(UUID invoiceId) {
    int score = calculateRiskScore(invoiceId);
    return score > RISK_THRESHOLD;
  }
}
