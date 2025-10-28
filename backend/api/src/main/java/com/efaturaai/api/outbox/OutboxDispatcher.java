package com.efaturaai.api.outbox;

import com.efaturaai.core.domain.OutboxMessage;
import com.efaturaai.core.domain.OutboxStatus;
import com.efaturaai.core.repository.OutboxRepository;
import com.efaturaai.core.tenant.TenantContext;
import com.efaturaai.infra.messaging.OutboxPublisher;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxDispatcher {

  private final OutboxRepository outboxRepository;
  private final OutboxPublisher publisher;

  public OutboxDispatcher(OutboxRepository outboxRepository, OutboxPublisher publisher) {
    this.outboxRepository = outboxRepository;
    this.publisher = publisher;
  }

  // For demo: iterate tenants externally in production; here we assume single-tenant per process
  @Scheduled(fixedDelayString = "${outbox.dispatch.interval.ms:2000}")
  @Transactional
  public void dispatch() {
    UUID tenantId = TenantContext.getTenantId().orElse(null);
    if (tenantId == null) {
      return; // tenant not set in scheduler context; in production, loop all tenants
    }
    List<OutboxMessage> ready =
        outboxRepository.findReady(OutboxStatus.NEW, OffsetDateTime.now(), tenantId);
    for (OutboxMessage msg : ready) {
      try {
        publisher.publish(msg);
        outboxRepository.updateStatus(msg.getId(), OutboxStatus.SENT, msg.getRetryCount());
      } catch (Exception ex) {
        int retry = msg.getRetryCount() + 1;
        outboxRepository.updateStatus(msg.getId(), OutboxStatus.FAILED, retry);
      }
    }
  }
}
