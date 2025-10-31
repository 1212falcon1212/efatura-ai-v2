package com.efaturaai.infra.messaging;

import com.efaturaai.core.domain.OutboxMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OutboxPublisher {

  private final RabbitTemplate rabbitTemplate;
  private final String exchange;
  private final String routingKey;

  public OutboxPublisher(
      RabbitTemplate rabbitTemplate,
      @Value("${messaging.outbox.exchange:outbox.exchange}") String exchange,
      @Value("${messaging.outbox.routing:outbox.routing}") String routingKey) {
    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
    this.routingKey = routingKey;
  }

  public void publish(OutboxMessage message) {
    rabbitTemplate.convertAndSend(
        exchange,
        routingKey,
        message.getPayload(),
        m -> {
          m.getMessageProperties().setHeader("outboxId", message.getId().toString());
          m.getMessageProperties().setHeader("eventType", message.getEventType());
          m.getMessageProperties().setHeader("aggregateType", message.getAggregateType());
          m.getMessageProperties().setHeader("aggregateId", message.getAggregateId());
          return m;
        });
  }
}
