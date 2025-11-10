package com.efaturaai.aiops;

import com.efaturaai.core.domain.AiErrorSample;
import com.efaturaai.core.repository.AiErrorSampleRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class ErrorClassifierService {
  private static final Logger log = LoggerFactory.getLogger(ErrorClassifierService.class);

  private static final Pattern TIMEOUT_PATTERN = Pattern.compile("(?i)(timeout|timed out|connection.*timeout)");
  private static final Pattern SOAP_FAULT_PATTERN = Pattern.compile("(?i)(soap.*fault|soapfault|wsdl.*error)");
  private static final Pattern UBL_PATTERN = Pattern.compile("(?i)(ubl|xml.*validation|schema.*error|xsd)");
  private static final Pattern SIGNING_PATTERN = Pattern.compile("(?i)(sign|signature|hsm|certificate|private.*key)");
  private static final Pattern RATE_LIMIT_PATTERN = Pattern.compile("(?i)(rate.*limit|too.*many|quota|429)");
  private static final Pattern AUTH_PATTERN = Pattern.compile("(?i)(auth|unauthorized|401|forbidden|403|credential)");
  private static final Pattern NETWORK_PATTERN = Pattern.compile("(?i)(network|connection.*refused|connection.*reset|host.*unreachable)");

  private final AiErrorSampleRepository errorSampleRepository;

  public ErrorClassifierService(AiErrorSampleRepository errorSampleRepository) {
    this.errorSampleRepository = errorSampleRepository;
  }

  public ClassificationResult classify(String message, String code, String contextJson, UUID tenantId, String source) {
    long startTime = System.currentTimeMillis();
    try {
      MDC.put("tenantId", tenantId.toString());
      MDC.put("source", source);

      ErrorType errorType = ErrorType.OTHER;
      BigDecimal confidence = BigDecimal.ZERO;
      SuggestedAction suggestedAction = SuggestedAction.CONTACT_SUPPORT;

      String lowerMessage = message != null ? message.toLowerCase() : "";
      String lowerCode = code != null ? code.toLowerCase() : "";

      // Code-based classification first (high confidence)
      if (code != null) {
        if ("1001".equals(code) || "401".equals(code)) {
          errorType = ErrorType.AUTH;
          confidence = new BigDecimal("0.95");
          suggestedAction = SuggestedAction.CHECK_HSM;
        } else if ("2001".equals(code) || "2002".equals(code)) {
          errorType = ErrorType.INVALID_UBL;
          confidence = new BigDecimal("0.90");
          suggestedAction = SuggestedAction.CHECK_UBL;
        } else if ("429".equals(code)) {
          errorType = ErrorType.RATE_LIMIT;
          confidence = new BigDecimal("0.95");
          suggestedAction = SuggestedAction.WAIT;
        }
      }

      // Message-based classification if code didn't match
      if (confidence.compareTo(BigDecimal.ZERO) == 0) {
        if (TIMEOUT_PATTERN.matcher(lowerMessage).find()) {
          errorType = ErrorType.TIMEOUT;
          confidence = new BigDecimal("0.85");
          suggestedAction = SuggestedAction.RETRY;
        } else if (SOAP_FAULT_PATTERN.matcher(lowerMessage).find()) {
          errorType = ErrorType.SOAP_FAULT;
          confidence = new BigDecimal("0.80");
          suggestedAction = SuggestedAction.RETRY;
        } else if (UBL_PATTERN.matcher(lowerMessage).find()) {
          errorType = ErrorType.INVALID_UBL;
          confidence = new BigDecimal("0.85");
          suggestedAction = SuggestedAction.CHECK_UBL;
        } else if (SIGNING_PATTERN.matcher(lowerMessage).find()) {
          errorType = ErrorType.SIGNING_ERROR;
          confidence = new BigDecimal("0.80");
          suggestedAction = SuggestedAction.CHECK_HSM;
        } else if (RATE_LIMIT_PATTERN.matcher(lowerMessage).find()) {
          errorType = ErrorType.RATE_LIMIT;
          confidence = new BigDecimal("0.90");
          suggestedAction = SuggestedAction.WAIT;
        } else if (AUTH_PATTERN.matcher(lowerMessage).find()) {
          errorType = ErrorType.AUTH;
          confidence = new BigDecimal("0.85");
          suggestedAction = SuggestedAction.CHECK_HSM;
        } else if (NETWORK_PATTERN.matcher(lowerMessage).find()) {
          errorType = ErrorType.NETWORK;
          confidence = new BigDecimal("0.75");
          suggestedAction = SuggestedAction.RETRY;
        } else {
          errorType = ErrorType.OTHER;
          confidence = new BigDecimal("0.50");
          suggestedAction = SuggestedAction.CONTACT_SUPPORT;
        }
      }

      // Record error sample
      AiErrorSample sample = new AiErrorSample();
      sample.setId(UUID.randomUUID());
      sample.setTenantId(tenantId);
      sample.setSource(source);
      sample.setCode(code);
      sample.setMessage(message);
      sample.setContextJson(contextJson);
      sample.setCreatedAt(OffsetDateTime.now());
      errorSampleRepository.save(sample);

      ClassificationResult result = new ClassificationResult(errorType, confidence, suggestedAction);
      MDC.put("errorType", errorType.name());
      MDC.put("confidence", confidence.toString());

      long duration = System.currentTimeMillis() - startTime;
      log.info("Error classified in {}ms: type={}, confidence={}, action={}", duration, errorType, confidence, suggestedAction);

      return result;
    } finally {
      MDC.clear();
    }
  }
}
