package com.efaturaai.infra.messaging;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListenerRetryConfig {

  @Bean
  public SimpleRabbitListenerContainerFactory outboxListenerContainerFactory(
      ConnectionFactory connectionFactory, RabbitTemplate rabbitTemplate) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    MessageRecoverer recoverer =
        new RepublishMessageRecoverer(rabbitTemplate, "outbox.dlx", "outbox.dlq");
    factory.setAdviceChain(
        RetryInterceptorBuilder.stateless()
            .maxAttempts(3)
            .backOffOptions(1000, 2.0, 10000)
            .recoverer(recoverer)
            .build());
    return factory;
  }

  @Bean
  public SimpleRabbitListenerContainerFactory notificationsListenerContainerFactory(
      ConnectionFactory connectionFactory, RabbitTemplate rabbitTemplate) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    MessageRecoverer recoverer =
        new RepublishMessageRecoverer(rabbitTemplate, "notifications.dlx", "notifications.dlq");
    factory.setAdviceChain(
        RetryInterceptorBuilder.stateless()
            .maxAttempts(3)
            .backOffOptions(1000, 2.0, 10000)
            .recoverer(recoverer)
            .build());
    return factory;
  }
}


