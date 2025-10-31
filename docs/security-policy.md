# Security Policy

## Overview

Bu dokümantasyon e-Fatura SaaS projesinin güvenlik politikalarını ve best practice'lerini içerir.

## Secrets Management

### Kubernetes Secrets

Production'da secrets şifreli olmalıdır:

#### SealedSecrets

```bash
# Install kubeseal
brew install kubeseal

# Create SealedSecret
kubectl create secret generic efatura-secrets \
  --from-literal=DATASOURCE_PASSWORD=secret \
  --dry-run=client -o yaml | \
  kubeseal -o yaml > sealed-secret.yaml
```

#### SOPS

```bash
# Install sops
brew install sops

# Encrypt secret
sops -e -i deploy/k8s/production/secret.yaml
```

### Environment Variables

- Secrets aslında environment variable olarak değil, Kubernetes Secret olarak yönetilmeli
- Git'e commit edilmemeli (`.gitignore` ile korunmalı)
- Rotation policy tanımlanmalı

## TLS Configuration

### Ingress TLS

Let's Encrypt ile otomatik TLS sertifikası:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  annotations:
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  tls:
    - hosts:
        - app.efatura.ai
      secretName: efatura-tls
```

### TLS Version

- Minimum TLS 1.2
- Preferred TLS 1.3
- Disable TLS 1.0 and 1.1

### HSTS

```yaml
nginx.ingress.kubernetes.io/hsts-max-age: "31536000"
nginx.ingress.kubernetes.io/hsts-include-subdomains: "true"
```

## CORS Configuration

### Allowed Origins

Sadece izinli origin'lere izin ver:

```yaml
nginx.ingress.kubernetes.io/cors-allow-origin: "https://app.efatura.ai"
nginx.ingress.kubernetes.io/cors-allow-methods: "GET, POST, PUT, DELETE, PATCH, OPTIONS"
nginx.ingress.kubernetes.io/cors-allow-headers: "Authorization, Content-Type, X-Requested-With"
nginx.ingress.kubernetes.io/cors-allow-credentials: "true"
```

### Backend CORS

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://app.efatura.ai"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        return new UrlBasedCorsConfigurationSource();
    }
}
```

## Database Security

### SSL Connection

PostgreSQL bağlantıları SSL ile olmalı:

```yaml
DATASOURCE_URL: "jdbc:postgresql://postgres:5432/efatura?ssl=true&sslmode=require"
```

### Connection Pooling

- Connection pool size limitli olmalı
- Idle connections timeout olmalı
- Password rotation policy olmalı

## RabbitMQ Security

### SSL/TLS

RabbitMQ bağlantıları SSL ile olmalı:

```yaml
spring:
  rabbitmq:
    ssl:
      enabled: true
      verify-hostname: true
```

### Authentication

- Default guest user disable edilmeli
- Strong password policy
- User rotation

## Container Security

### Non-root User

Tüm container'lar non-root user ile çalışmalı:

```yaml
securityContext:
  runAsNonRoot: true
  runAsUser: 1001
  fsGroup: 1001
```

### Read-only Root Filesystem

Mümkün olduğunca read-only root filesystem:

```yaml
securityContext:
  readOnlyRootFilesystem: true
```

### Capabilities

Minimal capabilities:

```yaml
securityContext:
  capabilities:
    drop:
      - ALL
    add:
      - NET_BIND_SERVICE  # Only if needed
```

## Network Policies

### Pod-to-Pod Communication

Sadece gerekli pod'lar arası iletişim:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
spec:
  podSelector:
    matchLabels:
      app: efatura-backend
  policyTypes:
    - Ingress
    - Egress
  ingress:
    - from:
        - podSelector:
            matchLabels:
              app: efatura-frontend
  egress:
    - to:
        - podSelector:
            matchLabels:
              app: postgres
```

## RBAC

### Service Account

Minimal permissions:

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: efatura-backend
---
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  name: efatura-backend
rules:
  - apiGroups: [""]
    resources: ["configmaps", "secrets"]
    verbs: ["get", "list"]
```

## API Security

### JWT

- Strong secret (minimum 256 bits)
- Short expiration time (default: 24 hours)
- Refresh token mechanism
- Token rotation

### Rate Limiting

API endpoint'leri için rate limiting:

```java
@Configuration
public class RateLimitConfig {
    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(100.0); // 100 requests per second
    }
}
```

### Input Validation

- Tüm input'lar validate edilmeli
- SQL injection koruması (JPA/Hibernate)
- XSS koruması
- CSRF koruması

## Audit Logging

### Security Events

Loglanması gereken event'ler:

- Authentication attempts (success/failure)
- Authorization failures
- Sensitive data access
- Configuration changes
- Secret access

### Log Format

```json
{
  "timestamp": "2024-01-01T12:00:00.000Z",
  "event": "AUTHENTICATION_SUCCESS",
  "userId": "user-123",
  "tenantId": "tenant-1",
  "ip": "192.168.1.1",
  "userAgent": "Mozilla/5.0..."
}
```

## Vulnerability Scanning

### Container Images

Docker image'leri vulnerability scan:

```bash
# Trivy
trivy image ghcr.io/efaturaai/backend:latest

# Snyk
snyk test --docker ghcr.io/efaturaai/backend:latest
```

### Dependency Scanning

- Backend: OWASP Dependency Check
- Frontend: npm audit

## Incident Response

### Security Incident Process

1. **Detection**: Monitor security events
2. **Response**: Isolate affected systems
3. **Investigation**: Analyze logs and traces
4. **Remediation**: Fix vulnerabilities
5. **Post-mortem**: Document lessons learned

### Contact

Security issues: security@efatura.ai

## Compliance

### GDPR

- Data encryption at rest
- Data encryption in transit
- Right to deletion
- Data portability
- Privacy by design

### Data Retention

- Invoice data: 10 years (legal requirement)
- Logs: 90 days
- Metrics: 1 year
- Traces: 7 days

## Best Practices

1. **Principle of Least Privilege**: Minimum required permissions
2. **Defense in Depth**: Multiple security layers
3. **Security by Design**: Security considerations from start
4. **Regular Updates**: Keep dependencies updated
5. **Security Testing**: Regular penetration testing
6. **Training**: Security awareness training
7. **Documentation**: Document security policies
8. **Monitoring**: Monitor security events
9. **Incident Response**: Prepared incident response plan
10. **Compliance**: Meet regulatory requirements

## References

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Kubernetes Security Best Practices](https://kubernetes.io/docs/concepts/security/)
- [NIST Cybersecurity Framework](https://www.nist.gov/cyberframework)