package com.efaturaai.infra.messaging;

import com.efaturaai.aiops.ClassificationRecorder;
import com.efaturaai.aiops.ErrorClassifierService;
import com.efaturaai.aiops.ErrorType;
import com.efaturaai.core.domain.Invoice;
import com.efaturaai.core.repository.InvoiceRepository;
import com.efaturaai.core.tenant.TenantContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InvoiceSendFailedConsumer {
  private static final Logger log = LoggerFactory.getLogger(InvoiceSendFailedConsumer.class);

  private final ErrorClassifierService errorClassifier;
  private final ClassificationRecorder classificationRecorder;
  private final InvoiceRepository invoiceRepository;
  private final ObjectMapper objectMapper;
  private final Counter errorClassificationCounter;
  private final MeterRegistry meterRegistry;

  public InvoiceSendFailedConsumer(
      ErrorClassifierService errorClassifier,
      ClassificationRecorder classificationRecorder,
      InvoiceRepository invoiceRepository,
      ObjectMapper objectMapper,
      MeterRegistry meterRegistry) {
    this.errorClassifier = errorClassifier;
    this.classificationRecorder = classificationRecorder;
    this.invoiceRepository = invoiceRepository;
    this.objectMapper = objectMapper;
    this.errorClassificationCounter = Counter.builder("ai_error_classification_total")
        .description("Total number of error classifications")
        .register(meterRegistry);
    this.meterRegistry = meterRegistry;
  }

  @RabbitListener(
      queues = "${messaging.outbox.queue:outbox.queue}",
      containerFactory = "outboxListenerContainerFactory")
  @Transactional
  public void consume(
      String payload,
      @Header(name = "eventType", required = false) String eventType,
      @Header(name = "outboxId", required = false) String outboxId) {
    try {
      // Only process InvoiceSendFailed events
      if (!"InvoiceSendFailed".equals(eventType)) {
        return;
      }

      // Parse payload to get invoiceId
      JsonNode payloadJson = objectMapper.readTree(payload);
      String invoiceIdStr = payloadJson.has("invoiceId") 
          ? payloadJson.get("invoiceId").asText()
          : null;
      
      if (invoiceIdStr == null) {
        log.warn("InvoiceSendFailed event missing invoiceId in payload");
        return;
      }

      UUID invoiceId = UUID.fromString(invoiceIdStr);
      Invoice invoice = invoiceRepository.findById(invoiceId)
          .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + invoiceId));

      TenantContext.setTenantId(invoice.getTenantId());
      MDC.put("tenantId", invoice.getTenantId().toString());
      MDC.put("invoiceId", invoiceId.toString());

      String errorMessage = invoice.getProviderResponseExplanation() != null
          ? invoice.getProviderResponseExplanation()
          : "Invoice send failed";
      String errorCode = invoice.getProviderResponseCode();

      // Classify error
      var result = errorClassifier.classify(
          errorMessage,
          errorCode,
          "{\"invoiceId\":\"" + invoiceId + "\",\"invoiceNo\":\"" + invoice.getInvoiceNo() + "\"}",
          invoice.getTenantId(),
          "invoice.send"
      );

      // Record classification
      UUID errorSampleId = classificationRecorder.createErrorSample(
          invoice.getTenantId(),
          "invoice.send",
          errorCode,
          errorMessage,
          "{\"invoiceId\":\"" + invoiceId + "\"}"
      );
      classificationRecorder.record(errorSampleId, invoice.getTenantId(), result);

      // Update metrics
      errorClassificationCounter.increment();
      Counter.builder("ai_error_count")
          .tag("type", result.errorType().name().toLowerCase())
          .register(meterRegistry)
          .increment();

      MDC.put("errorType", result.errorType().name());
      log.info("Invoice send failure classified: invoiceId={}, errorType={}, confidence={}, action={}",
          invoiceId, result.errorType(), result.confidence(), result.suggestedAction());

    } catch (Exception e) {
      log.error("Error processing InvoiceSendFailed event", e);
    } finally {
      MDC.clear();
      TenantContext.clear();
    }
  }
}
