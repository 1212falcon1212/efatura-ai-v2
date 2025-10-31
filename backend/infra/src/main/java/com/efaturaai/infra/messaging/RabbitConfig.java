package com.efaturaai.infra.messaging;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  @Bean
  public DirectExchange outboxExchange(
      @Value("${messaging.outbox.exchange:outbox.exchange}") String name) {
    return new DirectExchange(name, true, false);
  }

  @Bean
  public Queue outboxQueue(
      @Value("${messaging.outbox.queue:outbox.queue}") String name,
      @Value("${messaging.outbox.dlx:outbox.dlx}") String dlx,
      @Value("${messaging.outbox.dlq:outbox.dlq}") String dlqRouting) {
    return QueueBuilder.durable(name)
        .withArgument("x-dead-letter-exchange", dlx)
        .withArgument("x-dead-letter-routing-key", dlqRouting)
        .build();
  }

  @Bean
  public Binding outboxBinding(
      @Qualifier("outboxExchange") DirectExchange outboxExchange,
      @Qualifier("outboxQueue") Queue outboxQueue,
      @Value("${messaging.outbox.routing:outbox.routing}") String routing) {
    return BindingBuilder.bind(outboxQueue).to(outboxExchange).with(routing);
  }

  @Bean
  public DirectExchange outboxDlx(@Value("${messaging.outbox.dlx:outbox.dlx}") String name) {
    return new DirectExchange(name, true, false);
  }

  @Bean
  public Queue outboxDlq(@Value("${messaging.outbox.dlq:outbox.dlq}") String name) {
    return QueueBuilder.durable(name).build();
  }

  @Bean
  public Binding outboxDlqBinding(
      @Qualifier("outboxDlx") DirectExchange outboxDlx,
      @Qualifier("outboxDlq") Queue outboxDlq,
      @Value("${messaging.outbox.dlq:outbox.dlq}") String routing) {
    return BindingBuilder.bind(outboxDlq).to(outboxDlx).with(routing);
  }

  @Bean
  public TopicExchange notificationsExchange(
      @Value("${messaging.notifications.exchange:notifications.exchange}") String name) {
    return new TopicExchange(name, true, false);
  }

  @Bean
  public Queue notificationsQueue(
      @Value("${messaging.notifications.queue:notifications.queue}") String name,
      @Value("${messaging.notifications.dlx:notifications.dlx}") String dlx,
      @Value("${messaging.notifications.dlq:notifications.dlq}") String dlqRouting) {
    return QueueBuilder.durable(name)
        .withArgument("x-dead-letter-exchange", dlx)
        .withArgument("x-dead-letter-routing-key", dlqRouting)
        .build();
  }

  @Bean
  public Binding notificationsBinding(
      TopicExchange notificationsExchange,
      @Qualifier("notificationsQueue") Queue notificationsQueue,
      @Value("${messaging.notifications.routing:notifications.#}") String routing) {
    return BindingBuilder.bind(notificationsQueue).to(notificationsExchange).with(routing);
  }

  @Bean
  public DirectExchange notificationsDlx(
      @Value("${messaging.notifications.dlx:notifications.dlx}") String name) {
    return new DirectExchange(name, true, false);
  }

  @Bean
  public Queue notificationsDlq(
      @Value("${messaging.notifications.dlq:notifications.dlq}") String name) {
    return QueueBuilder.durable(name).build();
  }

  @Bean
  public Binding notificationsDlqBinding(
      @Qualifier("notificationsDlx") DirectExchange notificationsDlx,
      @Qualifier("notificationsDlq") Queue notificationsDlq,
      @Value("${messaging.notifications.dlq:notifications.dlq}") String routing) {
    return BindingBuilder.bind(notificationsDlq).to(notificationsDlx).with(routing);
  }
}
