package com.efaturaai.api.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.UUID;
import java.util.List;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;
import com.efaturaai.api.invoice.InvoiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {ItTestConfig.class})
public class SmokeIT extends TestcontainersSupport {

  @LocalServerPort int port;

  @Autowired TestRestTemplate rest;

  @Test
  void end_to_end_flow() {
    String base = "http://localhost:" + port;
    // health
    ResponseEntity<Map> health = rest.getForEntity(base + "/actuator/health", Map.class);
    assertThat(health.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(health.getBody()).isNotNull();
    assertThat(health.getBody().get("status")).isEqualTo("UP");

    // login
    UUID tenant = UUID.fromString("00000000-0000-0000-0000-000000000001");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("X-Tenant", tenant.toString());
    ResponseEntity<Map> login =
        rest.postForEntity(
            base + "/auth/login",
            new HttpEntity<>(
                Map.of(
                    "username", "demo",
                    "password", "demo",
                    "tenant", tenant.toString()),
                headers),
            Map.class);
    assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
    String token = (String) login.getBody().get("accessToken");

    // create invoice (capture logs for MDC assertion)
    Logger serviceLogger = (Logger) LoggerFactory.getLogger(InvoiceService.class);
    ListAppender<ILoggingEvent> appender = new ListAppender<>();
    appender.start();
    serviceLogger.addAppender(appender);

    // create invoice
    HttpHeaders auth = new HttpHeaders();
    auth.setBearerAuth(token);
    auth.add("X-Tenant", tenant.toString());
    ResponseEntity<Map> created =
        rest.exchange(
            base + "/invoices",
            HttpMethod.POST,
            new HttpEntity<>(Map.of("customerName", "Acme", "totalGross", 118), auth),
            Map.class);
    assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    String id = (String) created.getBody().get("id");

    // actuator metric assertion
    ResponseEntity<Map> metric =
        rest.getForEntity(base + "/actuator/metrics/invoices.created.count", Map.class);
    assertThat(metric.getStatusCode()).isEqualTo(HttpStatus.OK);
    List measurements = (List) metric.getBody().get("measurements");
    assertThat(measurements).isNotNull();
    Map first = (Map) measurements.get(0);
    Number val = (Number) first.get("value");
    assertThat(val.doubleValue()).isGreaterThan(0.0);

    // MDC contains tenantId
    boolean mdcOk = appender.list.stream()
        .anyMatch(ev -> tenant.toString().equals(ev.getMDCPropertyMap().get("tenantId")));
    assertThat(mdcOk).isTrue();

    // sign
    ResponseEntity<Map> signed =
        rest.exchange(
            base + "/invoices/" + id + "/sign", HttpMethod.POST, new HttpEntity<>(auth), Map.class);
    assertThat(signed.getStatusCode()).isEqualTo(HttpStatus.OK);
    // send
    ResponseEntity<Map> sent =
        rest.exchange(
            base + "/invoices/" + id + "/send", HttpMethod.POST, new HttpEntity<>(auth), Map.class);
    assertThat(sent.getStatusCode()).isEqualTo(HttpStatus.OK);

    // pdf
    ResponseEntity<byte[]> pdf =
        rest.exchange(
            base + "/invoices/" + id + "/pdf",
            HttpMethod.GET,
            new HttpEntity<>(auth),
            byte[].class);
    assertThat(pdf.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(pdf.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
  }

  @Test
  void provider_failure_creates_outbox_and_returns_500() {
    String base = "http://localhost:" + port;
    UUID tenant = UUID.fromString("00000000-0000-0000-0000-000000000001");
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("X-Tenant", tenant.toString());
    ResponseEntity<Map> login =
        rest.postForEntity(
            base + "/auth/login",
            new HttpEntity<>(
                Map.of(
                    "username", "demo",
                    "password", "demo",
                    "tenant", tenant.toString()),
                headers),
            Map.class);
    String token = (String) login.getBody().get("accessToken");

    HttpHeaders auth = new HttpHeaders();
    auth.setBearerAuth(token);
    auth.add("X-Tenant", tenant.toString());
    ResponseEntity<Map> created =
        rest.exchange(
            base + "/invoices",
            HttpMethod.POST,
            new HttpEntity<>(Map.of("customerName", "Acme", "totalGross", 118), auth),
            Map.class);
    String id = (String) created.getBody().get("id");

    // Switch provider bean to failing one via header (simulated by profile already)
    System.setProperty("it.provider.fail", "true");
    ResponseEntity<String> fail =
        rest.exchange(
            base + "/invoices/" + id + "/send",
            HttpMethod.POST,
            new HttpEntity<>(auth),
            String.class);
    assertThat(fail.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    System.setProperty("it.provider.fail", "false");
  }
}
