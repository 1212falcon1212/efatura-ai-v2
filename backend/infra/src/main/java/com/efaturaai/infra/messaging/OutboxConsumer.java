package com.efaturaai.infra.messaging;

import com.efaturaai.core.repository.OutboxRepository;
import java.util.UUID;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxConsumer {
  private final OutboxRepository repository;

  public OutboxConsumer(OutboxRepository repository) {
    this.repository = repository;
  }

  @RabbitListener(queues = "${messaging.outbox.queue:outbox.queue}")
  @Transactional
  public void consume(
      String payload, @Header(name = "outboxId", required = false) String outboxId) {
    if (outboxId != null) {
      repository.markProcessed(UUID.fromString(outboxId));
    }
  }
}
