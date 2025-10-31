package com.efaturaai.infra.webhook;

import com.efaturaai.core.domain.WebhookSubscription;
import com.efaturaai.core.repository.WebhookSubscriptionRepository;
import com.efaturaai.core.tenant.TenantContext;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class WebhookAdminService {
  private final WebhookSubscriptionRepository repo;

  public WebhookAdminService(WebhookSubscriptionRepository repo) {
    this.repo = repo;
  }

  public List<WebhookSubscription> list() {
    UUID tenant = TenantContext.getTenantId().orElseThrow();
    return repo.findAll().stream().filter(w -> tenant.equals(w.getTenantId())).toList();
  }

  public WebhookSubscription create(String eventType, String url, String secret) {
    UUID tenant = TenantContext.getTenantId().orElseThrow();
    WebhookSubscription w = new WebhookSubscription();
    w.setId(UUID.randomUUID());
    w.setTenantId(tenant);
    w.setEventType(eventType);
    w.setUrl(url);
    w.setSecret(secret);
    w.setActive(true);
    w.setCreatedAt(OffsetDateTime.now());
    return repo.save(w);
  }

  public void delete(UUID id) {
    repo.deleteById(id);
  }
}


