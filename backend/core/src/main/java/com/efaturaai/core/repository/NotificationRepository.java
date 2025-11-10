package com.efaturaai.core.repository;

import com.efaturaai.core.domain.Notification;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
  
  @Query("SELECT COUNT(n) FROM Notification n WHERE n.tenantId = :tenantId AND (n.readAt IS NULL OR n.status != 'READ')")
  long countUnreadByTenantId(@Param("tenantId") UUID tenantId);
}


