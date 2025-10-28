package com.efaturaai.core.jpa;

import com.efaturaai.core.tenant.TenantContext;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantFilterConfiguration implements PreInsertEventListener, PreUpdateEventListener {

  private final EntityManager entityManager;

  public TenantFilterConfiguration(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @PostConstruct
  public void registerListeners() {
    Session session = entityManager.unwrap(Session.class);
    SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
    EventListenerRegistry registry =
        sfi.getServiceRegistry().getService(EventListenerRegistry.class);
    registry.getEventListenerGroup(EventType.PRE_INSERT).appendListener(this);
    registry.getEventListenerGroup(EventType.PRE_UPDATE).appendListener(this);
  }

  // StatementInspector moved to TenantStatementInspector and configured via properties

  @Override
  public boolean onPreInsert(PreInsertEvent event) {
    return setTenantOnEntity(event.getState(), event.getPersister().getPropertyNames());
  }

  @Override
  public boolean onPreUpdate(PreUpdateEvent event) {
    return setTenantOnEntity(event.getState(), event.getPersister().getPropertyNames());
  }

  private boolean setTenantOnEntity(Object[] state, String[] propertyNames) {
    Optional<UUID> tenant = TenantContext.getTenantId();
    if (tenant.isEmpty()) return false;
    for (int i = 0; i < propertyNames.length; i++) {
      if ("tenantId".equals(propertyNames[i]) || "tenant_id".equals(propertyNames[i])) {
        state[i] = tenant.get();
        return false;
      }
    }
    return false;
  }
}
