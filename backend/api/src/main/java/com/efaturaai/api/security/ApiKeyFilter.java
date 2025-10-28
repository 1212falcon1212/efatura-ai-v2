package com.efaturaai.api.security;

import com.efaturaai.core.domain.ApiKey;
import com.efaturaai.core.repository.ApiKeyRepository;
import com.efaturaai.core.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {
  private final ApiKeyRepository apiKeyRepository;

  public ApiKeyFilter(ApiKeyRepository apiKeyRepository) {
    this.apiKeyRepository = apiKeyRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String key = request.getHeader("X-API-Key");
    if (key != null && !key.isBlank()) {
      Optional<ApiKey> found = apiKeyRepository.findActiveByKey(key);
      if (found.isPresent()) {
        ApiKey k = found.get();
        TenantContext.setTenantId(k.getTenantId());
        MDC.put("apiKeyId", k.getId().toString());
        Authentication auth = new ApiKeyAuthenticationToken(k);
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    }
    try {
      chain.doFilter(request, response);
    } finally {
      MDC.remove("apiKeyId");
    }
  }

  static class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
    private final ApiKey key;
    ApiKeyAuthenticationToken(ApiKey key) {
      super(java.util.List.of(new SimpleGrantedAuthority("ROLE_API")));
      this.key = key;
      setAuthenticated(true);
    }
    @Override public Object getCredentials() { return ""; }
    @Override public Object getPrincipal() { return key.getName(); }
  }
}


