# AI Layer Documentation

## Overview

Stage 3: AI & Automation Layer eklenmiştir. Bu katman aşağıdaki özellikleri sağlar:

1. **Hata Sınıflandırma**: SOAP/UBL/İmza/Ağ hatalarını otomatik sınıflandırır
2. **Akıllı Retry**: Policy engine ile akıllı yeniden deneme
3. **Risk Skoru**: Gönderim öncesi risk skoru hesaplama
4. **AI Insights**: Öneriler ve özetler

## Architecture

### Modules

- **ai-core**: Feature extraction ve risk scoring
- **ai-ops**: Error classification ve retry policy engine
- **ai-insights**: Insights ve raporlama
- **ai-gateway**: Model adapter (şimdilik kural tabanlı, ileride uzak modele genişler)

### Database Tables

- `ai_error_samples`: Hata örnekleri ve bağlamları
- `ai_classifications`: Hata sınıflandırma sonuçları
- `ai_retry_policies`: Retry policy kuralları
- `ai_features_invoice`: Fatura özellik vektörleri
- `ai_risk_scores`: Risk skorları

## API Endpoints

### Insights
- `GET /api/ai/insights` - Insight listesi (sayfalı)
- `GET /api/ai/insights/summary` - Özet istatistikler
- `GET /api/ai/insights/invoice/{invoiceId}` - Belirli fatura için insight

### Risk Scoring
- `GET /api/ai/risk/{invoiceId}` - Risk skoru

### Queue
- `POST /api/queue/retry/{invoiceId}` - Faturayı yeniden gönderme kuyruğuna ekle

## Error Types

- `TIMEOUT`: Zaman aşımı hataları
- `SOAP_FAULT`: SOAP hataları
- `INVALID_UBL`: UBL doğrulama hataları
- `SIGNING_ERROR`: İmza hataları
- `RATE_LIMIT`: Rate limiting hataları
- `AUTH`: Kimlik doğrulama hataları
- `NETWORK`: Ağ hataları
- `OTHER`: Diğer hatalar

## Suggested Actions

- `RETRY`: Yeniden deneme önerilir
- `CHECK_UBL`: UBL kontrolü gerekli
- `CHECK_HSM`: HSM kontrolü gerekli
- `WAIT`: Bekleme önerilir
- `CONTACT_SUPPORT`: Destek ile iletişime geç

## Metrics

Prometheus metrikleri:
- `ai_error_classification_total`: Toplam sınıflandırma sayısı
- `ai_error_count{type}`: Hata türüne göre sayı

## Frontend

`/ai/insights` sayfasında:
- Özet kartları (toplam hata, retry önerisi, başarı oranı)
- Hata dağılımı grafikleri (pie chart, bar chart)
- Insight kartları (filtreleme, arama)
- Retry butonu (tek tıkla yeniden gönderme)

## Testing

### Unit Tests
- ErrorClassifierService: 10+ örnek mesaj ile sınıflandırma testi
- RetryPolicyEngine: Policy kararı testi
- RiskScorer: Risk skoru hesaplama testi

### Integration Tests
- Testcontainers ile PostgreSQL + RabbitMQ testi
- Sentetik TIMEOUT senaryosu ile retry testi

### Frontend Tests
- Playwright smoke test: Insight sayfası görünürlüğü ve retry fonksiyonu

## Future Enhancements

1. **Machine Learning Model**: Kural tabanlı yerine ML model entegrasyonu
2. **Advanced Retry Policies**: Daha karmaşık retry stratejileri
3. **Real-time Insights**: WebSocket ile gerçek zamanlı insight'lar
4. **Predictive Analytics**: Hata tahmin modelleri
5. **A/B Testing**: Farklı retry stratejilerini test etme

## Configuration

Retry policy'ler `ai_retry_policies` tablosunda yönetilir:
- `max_attempts`: Maksimum deneme sayısı
- `backoff_initial_ms`: İlk backoff süresi (ms)
- `backoff_multiplier`: Backoff çarpanı
- `jitter_ms`: Jitter değeri (ms)
- `enabled`: Policy aktif/pasif

## Monitoring

Grafana dashboard: `docs/monitoring/ai-dashboard.json`

Dashboard panelleri:
- Error Classification Total
- Error Count by Type
- Error Rate Over Time
- Error Distribution
