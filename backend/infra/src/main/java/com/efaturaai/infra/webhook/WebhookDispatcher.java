package com.efaturaai.infra.webhook;

import com.efaturaai.core.domain.WebhookDelivery;
import com.efaturaai.core.repository.WebhookDeliveryRepository;
import com.efaturaai.core.repository.WebhookSubscriptionRepository;
import com.efaturaai.core.tenant.TenantContext;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WebhookDispatcher {
  private final WebhookDeliveryRepository deliveryRepository;
  private final WebhookService webhookService;

  public WebhookDispatcher(WebhookDeliveryRepository deliveryRepository, WebhookService webhookService) {
    this.deliveryRepository = deliveryRepository;
    this.webhookService = webhookService;
  }

  @Scheduled(fixedDelayString = "${webhooks.retry.interval.ms:5000}")
  @Transactional
  public void retryFailed() {
    UUID tenantId = TenantContext.getTenantId().orElse(null);
    if (tenantId == null) return;
    List<WebhookDelivery> pend = deliveryRepository.findPending(tenantId, OffsetDateTime.now());
    for (WebhookDelivery d : pend) {
      try {
        webhookService.publish(d.getEventType(), d.getPayload());
        d.setStatus("SENT");
        deliveryRepository.save(d);
      } catch (Exception ex) {
        int next = d.getAttempt() + 1;
        d.setAttempt(next);
        d.setNextRetryAt(OffsetDateTime.now().plus((long) Math.pow(2, next), ChronoUnit.SECONDS));
        deliveryRepository.save(d);
      }
    }
  }
}


