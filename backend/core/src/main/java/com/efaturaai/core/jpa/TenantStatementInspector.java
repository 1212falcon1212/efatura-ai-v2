package com.efaturaai.core.jpa;

import com.efaturaai.core.tenant.TenantContext;
import java.util.Optional;
import java.util.UUID;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public class TenantStatementInspector implements StatementInspector {

  @Override
  public String inspect(String sql) {
    Optional<UUID> tenant = TenantContext.getTenantId();
    if (tenant.isEmpty() || sql == null) {
      return sql;
    }
    String t = tenant.get().toString();
    String lower = sql.toLowerCase();
    if (lower.startsWith("select") && lower.contains(" tenant_id ")) {
      if (!lower.contains(" where ")) {
        return sql + " WHERE tenant_id = '" + t + "'";
      } else if (!lower.contains("tenant_id =")) {
        return sql + " AND tenant_id = '" + t + "'";
      }
    }
    if ((lower.startsWith("update") || lower.startsWith("delete"))
        && lower.contains(" tenant_id ")) {
      if (!lower.contains(" where ")) {
        return sql + " WHERE tenant_id = '" + t + "'";
      } else if (!lower.contains("tenant_id =")) {
        return sql + " AND tenant_id = '" + t + "'";
      }
    }
    return sql;
  }
}
