package com.efaturaai.aiops;

import com.efaturaai.core.domain.AiClassification;
import com.efaturaai.core.domain.AiErrorSample;
import com.efaturaai.core.repository.AiClassificationRepository;
import com.efaturaai.core.repository.AiErrorSampleRepository;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClassificationRecorder {
  private static final Logger log = LoggerFactory.getLogger(ClassificationRecorder.class);

  private final AiClassificationRepository classificationRepository;
  private final AiErrorSampleRepository errorSampleRepository;

  public ClassificationRecorder(
      AiClassificationRepository classificationRepository,
      AiErrorSampleRepository errorSampleRepository) {
    this.classificationRepository = classificationRepository;
    this.errorSampleRepository = errorSampleRepository;
  }

  @Transactional
  public void record(UUID errorSampleId, UUID tenantId, ClassificationResult result) {
    AiClassification classification = new AiClassification();
    classification.setId(UUID.randomUUID());
    classification.setErrorSampleId(errorSampleId);
    classification.setTenantId(tenantId);
    classification.setErrorType(result.errorType().name());
    classification.setConfidence(result.confidence());
    classification.setSuggestedAction(result.suggestedAction().name());
    classification.setCreatedAt(OffsetDateTime.now());
    classificationRepository.save(classification);
    log.debug("Classification recorded: errorSampleId={}, errorType={}, confidence={}", 
        errorSampleId, result.errorType(), result.confidence());
  }

  public UUID createErrorSample(UUID tenantId, String source, String code, String message, String contextJson) {
    AiErrorSample sample = new AiErrorSample();
    sample.setId(UUID.randomUUID());
    sample.setTenantId(tenantId);
    sample.setSource(source);
    sample.setCode(code);
    sample.setMessage(message);
    sample.setContextJson(contextJson);
    sample.setCreatedAt(OffsetDateTime.now());
    return errorSampleRepository.save(sample).getId();
  }
}
