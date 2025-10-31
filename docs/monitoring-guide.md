# Monitoring Guide

## Overview

Bu dokümantasyon e-Fatura SaaS projesinin monitoring ve observability yapılandırmasını içerir.

## Prometheus Metrics

### Backend Metrics Endpoint

Backend uygulaması Prometheus metriklerini `/actuator/prometheus` endpoint'inde expose eder.

### Available Metrics

#### HTTP Metrics
- `http_server_requests_seconds` - HTTP request duration histogram
- `http_server_requests_seconds_count` - Total HTTP requests
- `http_server_requests_seconds_sum` - Total HTTP request duration

#### JVM Metrics
- `jvm_memory_used_bytes` - JVM memory usage by area
- `jvm_memory_max_bytes` - Maximum JVM memory
- `jvm_threads_live_threads` - Active threads
- `jvm_gc_pause_seconds` - GC pause time
- `jvm_gc_pause_seconds_count` - GC pause count

#### Custom Metrics
- `ai_error_classification_total` - Total AI error classifications
- `ai_error_count{type}` - Error count by type
- `ai_retry_success_ratio` - Retry success ratio
- `invoice_send_duration` - Invoice send duration histogram
- `queue_messages_total` - Total messages processed

### Prometheus Configuration

```yaml
scrape_configs:
  - job_name: 'efatura-backend'
    kubernetes_sd_configs:
      - role: pod
        namespaces:
          names:
            - efatura
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_label_app]
        action: keep
        regex: efatura-backend
    metrics_path: /actuator/prometheus
    scrape_interval: 30s
```

## Grafana Dashboards

### Dashboard Import

1. Grafana UI'da "Dashboards" > "Import"
2. JSON dosyasını yapıştır veya dosyayı yükle
3. Prometheus datasource'u seç
4. Dashboard'u kaydet

### Available Dashboards

#### Backend Performance (`backend-performance.json`)
- Request rate
- Response time (p95, p99)
- Error rate
- JVM memory usage
- Active threads
- GC pause time

#### AI Layer Overview (`ai-layer-overview.json`)
- Error classification total
- Error count by type
- Error rate over time
- Error distribution

#### Queue Health (`queue-health.json`)
- Queue depth
- Message rate (published/delivered)
- Retry success ratio
- DLQ messages

## Alerting

### Alertmanager Configuration

```yaml
groups:
  - name: efatura_alerts
    interval: 30s
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.1
        for: 5m
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value }} errors/sec"

      - alert: HighMemoryUsage
        expr: jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} > 0.9
        for: 5m
        annotations:
          summary: "High memory usage"
          description: "Memory usage is {{ $value | humanizePercentage }}"

      - alert: PodDown
        expr: up{job="efatura-backend"} == 0
        for: 2m
        annotations:
          summary: "Backend pod is down"
          description: "Pod {{ $labels.pod }} is down"

      - alert: HighRetryRate
        expr: rate(ai_retry_attempts_count[5m]) > 10
        for: 5m
        annotations:
          summary: "High retry rate"
          description: "Retry rate is {{ $value }} retries/sec"
```

### Discord/Slack Webhook

```yaml
receivers:
  - name: discord
    webhook_configs:
      - url: 'https://discord.com/api/webhooks/YOUR_WEBHOOK_URL'
        send_resolved: true

  - name: slack
    slack_configs:
      - api_url: 'https://hooks.slack.com/services/YOUR/WEBHOOK/URL'
        channel: '#alerts'
        send_resolved: true
```

## Logging

### Log Format

Structured JSON logging format:

```json
{
  "timestamp": "2024-01-01T12:00:00.000Z",
  "level": "INFO",
  "traceId": "abc123",
  "tenantId": "tenant-1",
  "logger": "com.efaturaai.api.invoice.InvoiceService",
  "message": "Invoice created",
  "invoiceId": "inv-123"
}
```

### Log Collection

#### FluentBit Configuration

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: fluent-bit-config
data:
  fluent-bit.conf: |
    [SERVICE]
        Flush         1
        Log_Level     info
        Daemon        off
        Parsers_File  parsers.conf

    [INPUT]
        Name              tail
        Path              /var/log/containers/*efatura*.log
        Parser            docker
        Tag               efatura.*
        Refresh_Interval  5

    [FILTER]
        Name                kubernetes
        Match               efatura.*
        Kube_URL            https://kubernetes.default.svc:443
        Kube_CA_File        /var/run/secrets/kubernetes.io/serviceaccount/ca.crt
        Kube_Token_File     /var/run/secrets/kubernetes.io/serviceaccount/token
        Kube_Tag_Prefix     efatura.kubernetes.
        Merge_Log           On
        Keep_Log            Off

    [OUTPUT]
        Name        loki
        Match       efatura.*
        Host        loki.monitoring.svc
        Port        3100
        Labels      job=efatura
```

### Loki Query Examples

```logql
# Error logs
{app="efatura-backend"} |= "ERROR"

# Errors by tenant
{app="efatura-backend"} |= "ERROR" | json | tenantId="tenant-1"

# Trace logs
{app="efatura-backend"} | json | traceId="abc123"
```

## Distributed Tracing

### OpenTelemetry Configuration

Backend uygulaması OpenTelemetry ile Jaeger/Tempo'ya trace gönderir.

#### Jaeger Configuration

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: jaeger
spec:
  template:
    spec:
      containers:
        - name: jaeger
          image: jaegertracing/all-in-one:latest
          env:
            - name: COLLECTOR_OTLP_ENABLED
              value: "true"
          ports:
            - containerPort: 4318  # OTLP gRPC
            - containerPort: 4317  # OTLP HTTP
            - containerPort: 16686 # UI
```

### Trace Sampling

Production'da sampling probability: `0.1` (10%)

```yaml
management:
  tracing:
    sampling:
      probability: 0.1
```

## Health Checks

### Endpoints

- `/actuator/health` - Overall health
- `/actuator/health/readiness` - Readiness probe
- `/actuator/health/liveness` - Liveness probe

### Health Check Response

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "rabbit": {
      "status": "UP",
      "details": {
        "version": "3.13.0"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 100000000000,
        "free": 50000000000,
        "threshold": 10485760
      }
    }
  }
}
```

## Troubleshooting

### Metrics Not Appearing

1. Check Prometheus targets: `http://prometheus:9090/targets`
2. Verify `/actuator/prometheus` endpoint is accessible
3. Check service discovery configuration
4. Verify pod labels match scrape config

### Logs Not Appearing

1. Check FluentBit pods: `kubectl get pods -n monitoring`
2. Check Loki connectivity: `kubectl logs -n monitoring loki-0`
3. Verify log paths in FluentBit config
4. Check Loki queries: `http://loki:3100`

### Traces Not Appearing

1. Verify OpenTelemetry endpoint: `OTEL_EXPORTER_OTLP_ENDPOINT`
2. Check Jaeger collector logs
3. Verify sampling configuration
4. Check trace propagation headers

## Best Practices

1. **Metric Naming**: Use consistent naming conventions (`snake_case`)
2. **Label Cardinality**: Limit label values to prevent high cardinality
3. **Sampling**: Use appropriate sampling rates for traces
4. **Retention**: Configure appropriate retention periods for logs/metrics
5. **Alerts**: Set up alerts for critical metrics, avoid alert fatigue
6. **Dashboard Sharing**: Share dashboards with team members
7. **Documentation**: Document custom metrics and their purposes

## References

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [OpenTelemetry Documentation](https://opentelemetry.io/docs/)
- [Loki Documentation](https://grafana.com/docs/loki/)
