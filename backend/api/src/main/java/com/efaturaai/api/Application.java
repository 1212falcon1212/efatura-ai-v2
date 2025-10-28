package com.efaturaai.api;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.efaturaai")
@EnableJpaRepositories(basePackages = "com.efaturaai.core.repository")
@EntityScan(basePackages = "com.efaturaai.core.domain")
@EnableScheduling
@EnableRabbit
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
