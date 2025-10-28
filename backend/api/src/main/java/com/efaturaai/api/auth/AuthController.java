package com.efaturaai.api.auth;

import com.efaturaai.api.security.JwtService;
import com.efaturaai.core.tenant.TenantContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

  public record LoginRequest(
      @NotBlank String username, @NotBlank String password, @NotBlank String tenant) {}

  public record TokenResponse(String accessToken, String tokenType) {}

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Value("${security.permitAll:false}")
  private boolean permitAll;

  public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
    UUID tenantId = UUID.fromString(request.tenant());
    TenantContext.setTenantId(tenantId);
    if (!permitAll) {
      Authentication auth =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.username(), request.password()));
      SecurityContextHolder.getContext().setAuthentication(auth);
    }
    String token = jwtService.generate(tenantId, request.username(), Map.of(), 60);
    return ResponseEntity.ok(new TokenResponse(token, "Bearer"));
  }

  public record RefreshRequest(@NotBlank String token) {}

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
    var claims = jwtService.parse(request.token());
    UUID tenantId = UUID.fromString(claims.get("tenant", String.class));
    String subject = claims.getSubject();
    String token = jwtService.generate(tenantId, subject, Map.of(), 60);
    return ResponseEntity.ok(new TokenResponse(token, "Bearer"));
  }
}
