package com.efaturaai.infra.notification;

import com.efaturaai.core.domain.Notification;
import com.efaturaai.core.repository.NotificationRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationConsumer {
  private final NotificationRepository repository;

  public NotificationConsumer(NotificationRepository repository) {
    this.repository = repository;
  }

  @RabbitListener(
      queues = "${messaging.notifications.queue:notifications.queue}",
      containerFactory = "notificationsListenerContainerFactory")
  @Transactional
  public void consume(String payload, @Header(name = "notificationId", required = false) String id) {
    if (id == null) return;
    Optional<Notification> n = repository.findById(UUID.fromString(id));
    if (n.isPresent()) {
      Notification notif = n.get();
      notif.setStatus("DELIVERED");
      notif.setSentAt(OffsetDateTime.now());
      repository.save(notif);
    }
  }
}


