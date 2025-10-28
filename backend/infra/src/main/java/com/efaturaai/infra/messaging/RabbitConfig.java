package com.efaturaai.infra.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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
  public Queue outboxQueue(@Value("${messaging.outbox.queue:outbox.queue}") String name) {
    return new Queue(name, true);
  }

  @Bean
  public Binding outboxBinding(
      DirectExchange outboxExchange,
      Queue outboxQueue,
      @Value("${messaging.outbox.routing:outbox.routing}") String routing) {
    return BindingBuilder.bind(outboxQueue).to(outboxExchange).with(routing);
  }
}
