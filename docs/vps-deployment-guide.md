# VPS Deployment Guide - efatura.ai

## Genel Bakış

Bu rehber, e-Fatura SaaS sistemini VPS'e Docker Compose ile deploy etmek için adımları içerir.

## Gereksinimler

- VPS (Ubuntu 20.04+ önerilir)
- SSH erişimi
- Domain: efatura.ai (DNS yapılandırması)
- En az 4GB RAM, 2 CPU core, 50GB disk

## Adım 1: VPS Hazırlığı

### Docker ve Docker Compose Kurulumu

```bash
# Docker kurulumu
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Docker Compose kurulumu
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Docker servisini başlat
sudo systemctl start docker
sudo systemctl enable docker

# Docker Compose versiyonunu kontrol et
docker-compose --version
```

## Adım 2: Kod Çekme

```bash
# Proje dizinine git
cd /var/www  # veya istediğiniz dizin

# GitHub'dan clone
git clone https://github.com/1212falcon1212/efatura-ai-v2.git
cd efatura-ai-v2

# Main branch'e geç
git checkout main
```

## Adım 3: Environment Variables

`.env` dosyası oluştur:

```bash
cd /var/www/efatura-ai-v2
cp .env.example .env  # Eğer varsa
# veya manuel oluştur
nano .env
```

`.env` içeriği:

```env
# Database
POSTGRES_DB=efatura
POSTGRES_USER=efatura
POSTGRES_PASSWORD=GÜÇLÜ_ŞİFRE_BURAYA
POSTGRES_PORT=5432

# RabbitMQ
RABBITMQ_USER=efatura
RABBITMQ_PASSWORD=GÜÇLÜ_ŞİFRE_BURAYA
RABBITMQ_PORT=5672
RABBITMQ_MANAGEMENT_PORT=15672

# MinIO
MINIO_ACCESS_KEY=GÜÇLÜ_ACCESS_KEY_BURAYA
MINIO_SECRET_KEY=GÜÇLÜ_SECRET_KEY_BURAYA
MINIO_PORT=9000
MINIO_CONSOLE_PORT=9001
MINIO_BUCKET=efatura

# Backend
BACKEND_PORT=8080
JWT_SECRET=GÜÇLÜ_JWT_SECRET_BURAYA_32_KARAKTER
LIQUIBASE_ENABLED=true
LOG_LEVEL=INFO

# Provider (Kolaysoft)
PROVIDER_USERNAME=GERÇEK_KULLANICI_ADI
PROVIDER_PASSWORD=GERÇEK_ŞİFRE
PROVIDER_SOURCE_URN=

# Frontend
FRONTEND_PORT=80
```

## Adım 4: Docker Compose ile Çalıştırma

```bash
# Production compose dosyası ile başlat
docker-compose -f docker-compose.prod.yml up -d

# Logları kontrol et
docker-compose -f docker-compose.prod.yml logs -f

# Container durumunu kontrol et
docker-compose -f docker-compose.prod.yml ps
```

## Adım 5: Nginx Reverse Proxy Yapılandırması

### Nginx Kurulumu

```bash
sudo apt update
sudo apt install nginx certbot python3-certbot-nginx -y
```

### Nginx Config Dosyası

```bash
sudo nano /etc/nginx/sites-available/efatura.ai
```

İçerik:

```nginx
server {
    listen 80;
    server_name efatura.ai www.efatura.ai;

    # Let's Encrypt için
    location /.well-known/acme-challenge/ {
        root /var/www/html;
    }

    # Frontend ve Backend'e proxy
    location / {
        proxy_pass http://localhost:80;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }

    # API için direkt backend'e
    location /api {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

### Nginx'i Aktif Et

```bash
# Symlink oluştur
sudo ln -s /etc/nginx/sites-available/efatura.ai /etc/nginx/sites-enabled/

# Test et
sudo nginx -t

# Nginx'i yeniden başlat
sudo systemctl restart nginx
```

## Adım 6: SSL Sertifikası (Let's Encrypt)

```bash
# SSL sertifikası al
sudo certbot --nginx -d efatura.ai -d www.efatura.ai

# Otomatik yenileme test et
sudo certbot renew --dry-run
```

## Adım 7: DNS Yapılandırması

Domain sağlayıcınızda DNS kayıtları:

```
A Record: efatura.ai -> VPS_IP_ADRESI
A Record: www.efatura.ai -> VPS_IP_ADRESI
```

## Adım 8: Firewall Yapılandırması

```bash
# UFW firewall kurulumu
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw enable

# Durumu kontrol et
sudo ufw status
```

## Adım 9: Verification

```bash
# Health check
curl https://efatura.ai/actuator/health

# Frontend kontrolü
curl https://efatura.ai/

# API kontrolü
curl https://efatura.ai/api/actuator/health
```

## Adım 10: Otomatik Güncelleme (Opsiyonel)

### GitHub Actions ile Otomatik Deploy

VPS'e GitHub Actions'tan deploy için:

1. VPS'e SSH key ekle
2. GitHub Actions workflow'una deploy step ekle
3. VPS'te webhook veya git pull ile güncelleme

### Manuel Güncelleme

```bash
cd /var/www/efatura-ai-v2
git pull origin main
docker-compose -f docker-compose.prod.yml up -d --build
docker-compose -f docker-compose.prod.yml restart
```

## Troubleshooting

### Container'lar başlamıyor

```bash
# Logları kontrol et
docker-compose -f docker-compose.prod.yml logs

# Container'ları yeniden başlat
docker-compose -f docker-compose.prod.yml restart

# Tüm container'ları durdur ve başlat
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up -d
```

### Database bağlantı sorunu

```bash
# PostgreSQL container'ını kontrol et
docker-compose -f docker-compose.prod.yml ps postgres
docker-compose -f docker-compose.prod.yml logs postgres

# Backend environment variables kontrol et
docker-compose -f docker-compose.prod.yml exec backend env | grep DATASOURCE
```

### Nginx proxy sorunu

```bash
# Nginx config test
sudo nginx -t

# Nginx logları
sudo tail -f /var/log/nginx/error.log
sudo tail -f /var/log/nginx/access.log
```

## Backup

### Database Backup

```bash
# PostgreSQL backup
docker-compose -f docker-compose.prod.yml exec postgres pg_dump -U efatura efatura > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore
docker-compose -f docker-compose.prod.yml exec -T postgres psql -U efatura efatura < backup.sql
```

### Volume Backup

```bash
# Docker volume'ları yedekle
docker run --rm -v efatura-ai-v2_pgdata:/data -v $(pwd):/backup alpine tar czf /backup/pgdata_backup.tar.gz /data
docker run --rm -v efatura-ai-v2_miniodata:/data -v $(pwd):/backup alpine tar czf /backup/miniodata_backup.tar.gz /data
```

## Monitoring

### Container Durumu

```bash
# Container durumunu kontrol et
docker-compose -f docker-compose.prod.yml ps

# Resource kullanımı
docker stats
```

### Log Monitoring

```bash
# Tüm loglar
docker-compose -f docker-compose.prod.yml logs -f

# Sadece backend
docker-compose -f docker-compose.prod.yml logs -f backend

# Son 100 satır
docker-compose -f docker-compose.prod.yml logs --tail=100 backend
```

## Güvenlik

1. ✅ Güçlü şifreler kullanın
2. ✅ JWT_SECRET'ı güçlü yapın (32+ karakter)
3. ✅ Firewall aktif olsun
4. ✅ SSL sertifikası kullanın
5. ✅ Düzenli backup alın
6. ✅ Container'ları güncel tutun

## Sonraki Adımlar

1. Monitoring kurulumu (Prometheus + Grafana)
2. Log aggregation (Loki veya ELK)
3. Alerting yapılandırması
4. Backup otomasyonu
5. CI/CD pipeline entegrasyonu

