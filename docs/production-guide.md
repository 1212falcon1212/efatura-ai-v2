# Production Deployment Guide

## Overview

Bu dokümantasyon e-Fatura SaaS projesinin production ortamına deploy edilmesi için gereken adımları içerir.

## Prerequisites

- Kubernetes cluster (v1.24+)
- kubectl configured
- Docker registry access (GHCR/Docker Hub)
- cert-manager installed
- nginx-ingress-controller installed
- Prometheus operator (optional, for monitoring)

## Environment Variables

### Backend Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DATASOURCE_URL` | PostgreSQL connection URL | - | Yes |
| `DATASOURCE_USERNAME` | Database username | - | Yes |
| `DATASOURCE_PASSWORD` | Database password | - | Yes |
| `RABBITMQ_HOST` | RabbitMQ host | rabbitmq | Yes |
| `RABBITMQ_USERNAME` | RabbitMQ username | guest | Yes |
| `RABBITMQ_PASSWORD` | RabbitMQ password | guest | Yes |
| `MINIO_ENDPOINT` | MinIO endpoint | http://minio:9000 | Yes |
| `MINIO_ACCESS_KEY` | MinIO access key | - | Yes |
| `MINIO_SECRET_KEY` | MinIO secret key | - | Yes |
| `JWT_SECRET` | JWT signing secret | - | Yes |
| `PROVIDER_USERNAME` | Kolaysoft username | - | Yes |
| `PROVIDER_PASSWORD` | Kolaysoft password | - | Yes |
| `PROVIDER_SOURCE_URN` | Provider source URN | - | No |
| `SPRING_PROFILES_ACTIVE` | Spring profile | prod | Yes |
| `LIQUIBASE_ENABLED` | Run Liquibase migrations | false | Yes |

### Frontend Environment Variables

Frontend uses environment variables at build time. Set `VITE_API_URL` in CI/CD pipeline.

## Deployment Steps

### 1. Prepare Secrets

```bash
# Create namespace
kubectl create namespace efatura

# Create secrets (update values!)
kubectl apply -f deploy/k8s/production/secret.yaml
```

**Important:** Update secret values before applying!

### 2. Create ConfigMap

```bash
kubectl apply -f deploy/k8s/production/configmap.yaml
```

### 3. Deploy Infrastructure

```bash
# PostgreSQL
kubectl apply -f deploy/k8s/production/postgres.yaml

# RabbitMQ
kubectl apply -f deploy/k8s/production/rabbitmq.yaml

# MinIO
kubectl apply -f deploy/k8s/production/minio.yaml
```

### 4. Deploy Applications

```bash
# Backend
kubectl apply -f deploy/k8s/production/backend-deployment.yaml
kubectl apply -f deploy/k8s/production/backend-service.yaml

# Frontend
kubectl apply -f deploy/k8s/production/frontend-deployment.yaml
kubectl apply -f deploy/k8s/production/frontend-service.yaml
```

### 5. Configure Ingress

```bash
# Update ingress.yaml with your domain
kubectl apply -f deploy/k8s/production/ingress.yaml
```

### 6. Verify Deployment

```bash
# Check pods
kubectl get pods -n efatura

# Check services
kubectl get svc -n efatura

# Check ingress
kubectl get ingress -n efatura

# Check backend health
curl https://app.efatura.ai/actuator/health
```

## CI/CD Pipeline

### GitHub Actions

1. Push to `develop` branch → Deploy to staging
2. Push to `main` branch → Deploy to production (requires approval)

### Manual Deployment

```bash
# Build and push images
docker build -t ghcr.io/efaturaai/backend:latest ./backend
docker push ghcr.io/efaturaai/backend:latest

docker build -t ghcr.io/efaturaai/frontend:latest ./frontend
docker push ghcr.io/efaturaai/frontend:latest

# Update deployment
kubectl set image deployment/efatura-backend api=ghcr.io/efaturaai/backend:latest -n efatura
kubectl set image deployment/efatura-frontend nginx=ghcr.io/efaturaai/frontend:latest -n efatura

# Rollout status
kubectl rollout status deployment/efatura-backend -n efatura
```

## Rollback Procedure

```bash
# View rollout history
kubectl rollout history deployment/efatura-backend -n efatura

# Rollback to previous version
kubectl rollout undo deployment/efatura-backend -n efatura

# Rollback to specific revision
kubectl rollout undo deployment/efatura-backend --to-revision=2 -n efatura
```

## Health Checks

### Backend Health Endpoints

- `/actuator/health` - Overall health
- `/actuator/health/readiness` - Readiness probe
- `/actuator/health/liveness` - Liveness probe
- `/actuator/info` - Application info
- `/actuator/metrics` - Metrics
- `/actuator/prometheus` - Prometheus metrics

### Frontend Health Endpoint

- `/health` - Simple health check

## Monitoring

### Prometheus Metrics

Backend exposes metrics at `/actuator/prometheus`:

- `http_server_requests_seconds` - HTTP request metrics
- `jvm_memory_used_bytes` - JVM memory usage
- `jvm_threads_live_threads` - Active threads
- `jvm_gc_pause_seconds` - GC pause time
- `ai_error_classification_total` - AI error classifications
- `ai_error_count{type}` - Error count by type
- `ai_retry_success_ratio` - Retry success ratio

### Grafana Dashboards

- `backend-performance.json` - Backend performance metrics
- `ai-layer-overview.json` - AI layer metrics
- `queue-health.json` - Queue and messaging metrics

## Troubleshooting

### Pod Not Starting

```bash
# Check pod logs
kubectl logs -f deployment/efatura-backend -n efatura

# Check pod events
kubectl describe pod <pod-name> -n efatura

# Check resource limits
kubectl top pod <pod-name> -n efatura
```

### Database Connection Issues

```bash
# Test PostgreSQL connection
kubectl exec -it deployment/postgres -n efatura -- psql -U efatura -d efatura

# Check connection string
kubectl get secret efatura-secrets -n efatura -o jsonpath='{.data.DATASOURCE_URL}' | base64 -d
```

### RabbitMQ Issues

```bash
# Check RabbitMQ status
kubectl exec -it deployment/rabbitmq -n efatura -- rabbitmqctl status

# List queues
kubectl exec -it deployment/rabbitmq -n efatura -- rabbitmqctl list_queues
```

## Security Considerations

1. **Secrets Management**: Use SealedSecrets or SOPS for production
2. **TLS**: Ensure cert-manager is configured for Let's Encrypt
3. **Network Policies**: NetworkPolicy resources restrict pod-to-pod communication
4. **Non-root**: Containers run as non-root user (UID 1001)
5. **Resource Limits**: Set appropriate CPU/memory limits
6. **Security Scanning**: Scan Docker images for vulnerabilities

## Scaling

### Horizontal Scaling

```bash
# Scale backend
kubectl scale deployment efatura-backend --replicas=3 -n efatura

# Scale frontend
kubectl scale deployment efatura-frontend --replicas=3 -n efatura
```

### Vertical Scaling

Update resource requests/limits in deployment YAML files.

## Backup & Recovery

### PostgreSQL Backup

```bash
# Create backup
kubectl exec -it deployment/postgres -n efatura -- pg_dump -U efatura efatura > backup.sql

# Restore backup
kubectl exec -i deployment/postgres -n efatura -- psql -U efatura efatura < backup.sql
```

### MinIO Backup

MinIO data is stored in PersistentVolumeClaim. Backup PVC or use MinIO's built-in backup features.

## Performance Tuning

### JVM Options

Current JVM options:
```
-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=100
```

Adjust based on available resources and workload.

### Database Connection Pool

Default settings:
- `maximum-pool-size: 20`
- `minimum-idle: 5`

Adjust based on database capacity and load.

## Updates

### Rolling Updates

Deployments use RollingUpdate strategy:
- `maxSurge: 1` - One extra pod during update
- `maxUnavailable: 0` - Zero downtime updates

### Blue-Green Deployment

For zero-downtime deployments, consider blue-green strategy:

```bash
# Deploy new version with different label
kubectl apply -f backend-deployment-v2.yaml

# Switch traffic
kubectl patch service efatura-backend -p '{"spec":{"selector":{"version":"v2"}}}'
```

## Support

For issues or questions:
1. Check logs: `kubectl logs -f deployment/efatura-backend -n efatura`
2. Check metrics: Grafana dashboards
3. Check health: `/actuator/health` endpoint
4. Review documentation: `docs/` directory