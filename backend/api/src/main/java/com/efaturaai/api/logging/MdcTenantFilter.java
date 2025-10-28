package com.efaturaai.api.logging;

import com.efaturaai.core.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class MdcTenantFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Optional<UUID> tid = TenantContext.getTenantId();
    tid.ifPresent(uuid -> MDC.put("tenantId", uuid.toString()));
    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.remove("tenantId");
    }
  }
}
