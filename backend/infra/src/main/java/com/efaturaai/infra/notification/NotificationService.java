package com.efaturaai.infra.notification;

import com.efaturaai.core.domain.Notification;
import com.efaturaai.core.repository.NotificationRepository;
import com.efaturaai.core.tenant.TenantContext;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  private final NotificationRepository repository;
  private final RabbitTemplate rabbitTemplate;
  private final String exchange;
  private final String routingPattern;

  public NotificationService(
      NotificationRepository repository,
      RabbitTemplate rabbitTemplate,
      @Value("${messaging.notifications.exchange:notifications.exchange}") String exchange,
      @Value("${messaging.notifications.routing:notifications.#}") String routingPattern) {
    this.repository = repository;
    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
    this.routingPattern = routingPattern;
  }

  public Notification notify(String type, String channel, String payloadJson) {
    UUID tenantId = TenantContext.getTenantId().orElseThrow();
    Notification n = new Notification();
    n.setId(UUID.randomUUID());
    n.setTenantId(tenantId);
    n.setType(type);
    n.setChannel(channel);
    n.setPayload(payloadJson);
    n.setStatus("QUEUED");
    n.setCreatedAt(OffsetDateTime.now());
    repository.save(n);

    String routing = ("notifications." + tenantId + "." + type).toLowerCase();
    rabbitTemplate.convertAndSend(exchange, routing, payloadJson, m -> {
      m.getMessageProperties().setHeader("notificationId", n.getId().toString());
      m.getMessageProperties().setHeader("tenantId", tenantId.toString());
      m.getMessageProperties().setContentType("application/json");
      return m;
    });
    return n;
  }
}


