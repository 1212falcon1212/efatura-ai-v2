package com.efaturaai.infra.webhook;

import com.efaturaai.core.domain.WebhookDelivery;
import com.efaturaai.core.domain.WebhookSubscription;
import com.efaturaai.core.repository.WebhookDeliveryRepository;
import com.efaturaai.core.repository.WebhookSubscriptionRepository;
import com.efaturaai.core.tenant.TenantContext;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {
  private final WebhookSubscriptionRepository subRepo;
  private final WebhookDeliveryRepository deliveryRepo;
  private final RestTemplate restTemplate = new RestTemplate();
  private final ObjectMapper objectMapper = new ObjectMapper();

  public WebhookService(WebhookSubscriptionRepository subRepo, WebhookDeliveryRepository deliveryRepo) {
    this.subRepo = subRepo;
    this.deliveryRepo = deliveryRepo;
  }

  public void publish(String eventType, String jsonPayload) {
    UUID tenantId = TenantContext.getTenantId().orElse(null);
    if (tenantId == null) return;
    List<WebhookSubscription> targets = subRepo.findActiveByTenantAndEvent(tenantId, eventType);
    for (WebhookSubscription s : targets) {
      try {
        String signature = hmacSha256(jsonPayload, s.getSecret());
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.add("X-Signature", signature);
        HttpEntity<String> req = new HttpEntity<>(jsonPayload, h);
        ResponseEntity<String> resp = restTemplate.postForEntity(URI.create(s.getUrl()), req, String.class);
        WebhookDelivery d = new WebhookDelivery();
        d.setId(UUID.randomUUID());
        d.setTenantId(tenantId);
        d.setWebhookId(s.getId());
        d.setEventType(eventType);
        d.setPayload(jsonPayload);
        d.setStatus("SENT");
        d.setAttempt(1);
        d.setCreatedAt(OffsetDateTime.now());
        deliveryRepo.save(d);
      } catch (Exception ex) {
        WebhookDelivery d = new WebhookDelivery();
        d.setId(UUID.randomUUID());
        d.setTenantId(tenantId);
        d.setWebhookId(s.getId());
        d.setEventType(eventType);
        d.setPayload(jsonPayload);
        d.setStatus("FAILED");
        d.setAttempt(1);
        d.setNextRetryAt(OffsetDateTime.now().plusSeconds(2));
        d.setCreatedAt(OffsetDateTime.now());
        deliveryRepo.save(d);
      }
    }
  }

  public List<WebhookDelivery> listDeliveries(UUID webhookId) {
    UUID tenantId = TenantContext.getTenantId().orElseThrow();
    return deliveryRepo.findByWebhookIdAndTenantIdOrderByCreatedAtDesc(webhookId, tenantId);
  }

  public void retryDelivery(UUID deliveryId) {
    UUID tenantId = TenantContext.getTenantId().orElseThrow();
    WebhookDelivery d = deliveryRepo.findById(deliveryId).orElseThrow();
    if (!tenantId.equals(d.getTenantId())) return;
    WebhookSubscription s = subRepo.findById(d.getWebhookId()).orElseThrow();
    String payload = d.getPayload();
    try {
      String signature = hmacSha256(payload, s.getSecret());
      HttpHeaders h = new HttpHeaders();
      h.setContentType(MediaType.APPLICATION_JSON);
      h.add("X-Signature", signature);
      HttpEntity<String> req = new HttpEntity<>(payload, h);
      restTemplate.postForEntity(URI.create(s.getUrl()), req, String.class);
      d.setStatus("SENT");
      d.setAttempt(d.getAttempt() + 1);
      d.setNextRetryAt(null);
    } catch (Exception ex) {
      d.setStatus("FAILED");
      d.setAttempt(d.getAttempt() + 1);
      d.setNextRetryAt(OffsetDateTime.now().plusSeconds( (long) Math.pow(2, Math.min(6, d.getAttempt())) ));
    } finally {
      deliveryRepo.save(d);
    }
  }

  private String hmacSha256(String data, String secret) {
    try {
      Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
      SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      sha256_HMAC.init(secret_key);
      byte[] raw = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(raw);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}


