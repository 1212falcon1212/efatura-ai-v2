package com.efaturaai.core.audit;

import com.efaturaai.core.domain.AuditLog;
import com.efaturaai.core.repository.AuditLogRepository;
import com.efaturaai.core.tenant.TenantContext;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
public class AuditAspect {

  private final AuditLogRepository repository;

  public AuditAspect(AuditLogRepository repository) {
    this.repository = repository;
  }

  @AfterReturning("@annotation(audit)")
  @Transactional
  public void after(JoinPoint jp, Audit audit) {
    UUID tenantId = TenantContext.getTenantId().orElse(null);
    AuditLog log = new AuditLog();
    log.setId(UUID.randomUUID());
    log.setTenantId(tenantId);
    log.setEntityType(audit.entityType());
    log.setEntityId(audit.entityId());
    log.setAction(audit.action());
    log.setCreatedAt(OffsetDateTime.now());
    String apiKeyId = MDC.get("apiKeyId");
    if (apiKeyId != null) {
      log.setMetadata("{\"apiKeyId\":\"" + apiKeyId + "\"}");
    }
    repository.save(log);
  }
}
