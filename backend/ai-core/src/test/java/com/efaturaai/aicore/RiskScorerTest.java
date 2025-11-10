package com.efaturaai.aicore;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.efaturaai.core.domain.AiFeaturesInvoice;
import com.efaturaai.core.domain.AiRiskScore;
import com.efaturaai.core.domain.Invoice;
import com.efaturaai.core.repository.AiFeaturesInvoiceRepository;
import com.efaturaai.core.repository.AiRiskScoreRepository;
import com.efaturaai.core.repository.InvoiceRepository;
import io.micrometer.core.instrument.MeterRegistry;

import static org.mockito.Mockito.*;

class RiskScorerTest {

  @Mock
  private AiRiskScoreRepository riskScoreRepository;

  @Mock
  private InvoiceRepository invoiceRepository;

  @Mock
  private FeatureExtractorService featureExtractor;

  @Mock
  private MeterRegistry meterRegistry;

  private RiskScorer riskScorer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    riskScorer = new RiskScorer(riskScoreRepository, invoiceRepository, featureExtractor);
  }

  @Test
  void shouldCalculateRiskScore() {
    UUID invoiceId = UUID.randomUUID();
    Invoice invoice = new Invoice();
    invoice.setId(invoiceId);
    invoice.setTenantId(UUID.randomUUID());

    AiFeaturesInvoice features = new AiFeaturesInvoice();
    features.setId(UUID.randomUUID());
    features.setInvoiceId(invoiceId);
    features.setItemCount(25);
    features.setTotalAmount(new BigDecimal("60000"));
    features.setPreviousFailRate(new BigDecimal("0.15"));
    features.setEndpointLatencyMs(6000L);
    features.setHourOfDay(23);

    when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
    when(riskScoreRepository.findByInvoiceId(invoiceId)).thenReturn(Optional.empty());
    when(featureExtractor.extractFeatures(invoiceId)).thenReturn(features);

    int score = riskScorer.calculateRiskScore(invoiceId);
    assertTrue(score >= 0 && score <= 100);
  }

  @Test
  void shouldIdentifyHighRisk() {
    UUID invoiceId = UUID.randomUUID();
    Invoice invoice = new Invoice();
    invoice.setId(invoiceId);
    invoice.setTenantId(UUID.randomUUID());

    AiFeaturesInvoice features = new AiFeaturesInvoice();
    features.setId(UUID.randomUUID());
    features.setInvoiceId(invoiceId);
    features.setItemCount(100);
    features.setTotalAmount(new BigDecimal("200000"));
    features.setPreviousFailRate(new BigDecimal("0.30"));
    features.setEndpointLatencyMs(10000L);
    features.setHourOfDay(3);

    when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
    when(riskScoreRepository.findByInvoiceId(invoiceId)).thenReturn(Optional.empty());
    when(featureExtractor.extractFeatures(invoiceId)).thenReturn(features);

    boolean isHighRisk = riskScorer.isHighRisk(invoiceId);
    assertTrue(isHighRisk);
  }

  @Test
  void shouldReturnExistingScoreIfAvailable() {
    UUID invoiceId = UUID.randomUUID();
    Invoice invoice = new Invoice();
    invoice.setId(invoiceId);
    invoice.setTenantId(UUID.randomUUID());

    AiRiskScore existingScore = new AiRiskScore();
    existingScore.setId(UUID.randomUUID());
    existingScore.setInvoiceId(invoiceId);
    existingScore.setScore0100(75);

    when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
    when(riskScoreRepository.findByInvoiceId(invoiceId)).thenReturn(Optional.of(existingScore));

    int score = riskScorer.calculateRiskScore(invoiceId);
    assertEquals(75, score);
    verify(featureExtractor, never()).extractFeatures(invoiceId);
  }
}
