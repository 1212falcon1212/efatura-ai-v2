package com.efaturaai.api.security;

import com.efaturaai.core.tenant.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantExtractionFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  public TenantExtractionFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String tenantHeader = request.getHeader("X-Tenant");
      if (tenantHeader != null && !tenantHeader.isBlank()) {
        try {
          TenantContext.setTenantId(UUID.fromString(tenantHeader));
        } catch (Exception ignored) {
        }
      }
      String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
      if (auth != null && auth.startsWith("Bearer ")) {
        String token = auth.substring(7);
        Claims claims = jwtService.parse(token);
        Object tenant = claims.get("tenant");
        if (tenant != null) {
          UUID tid = UUID.fromString(tenant.toString());
          TenantContext.setTenantId(tid);
        }
      }
      filterChain.doFilter(request, response);
    } finally {
      TenantContext.clear();
    }
  }
}
