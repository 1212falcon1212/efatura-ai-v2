# Log Dosyalarını Görüntüleme Kılavuzu

## 1. Docker Container İçindeki Log Dosyasını Görüntüleme

### Son 100 satırı göster:
```bash
docker exec efatura-backend tail -100 /app/logs/app.log
```

### Canlı takip (tail -f gibi):
```bash
docker exec efatura-backend tail -f /app/logs/app.log
```

### JSON formatını okunabilir hale getir:
```bash
docker exec efatura-backend cat /app/logs/app.log | jq .
```

### Sadece fatura ile ilgili logları filtrele:
```bash
docker exec efatura-backend grep -i "invoice\|fatura" /app/logs/app.log | tail -50
```

### Belirli bir fatura ID'sine göre filtrele:
```bash
docker exec efatura-backend grep "YOUR-INVOICE-ID" /app/logs/app.log
```

## 2. Docker Compose Loglarını Kullanma

### Backend loglarını göster:
```bash
docker compose logs backend
```

### Son 200 satır ve canlı takip:
```bash
docker compose logs --tail=200 -f backend
```

### Sadece ERROR seviyesindeki loglar:
```bash
docker compose logs backend | grep -i error
```

## 3. Log Dosyasını Local'e Kopyalama

### Container'dan local'e kopyala:
```bash
docker cp efatura-backend:/app/logs/app.log ./logs/app.log
```

Sonra local dosyayı açabilirsiniz.

## 4. Fatura Kesme İşlemlerini İzleme

Fatura kesme işlemlerinde şu loglar görünecek:

### Fatura Oluşturma:
- Invoice created with full details
- Invoice summary (tutar bilgileri)
- Invoice details (internet satış, teslimat)
- Online sale info (varsa)
- Cargo/Courier info (varsa)
- Invoice lines (DEBUG seviyesinde)

### Fatura İmzalama:
- Signing invoice
- InvoiceDto created
- UBL XML generated
- UBL XML preview (ilk 500 karakter)
- UBL XML validation successful
- Invoice signed successfully

### Fatura Gönderme:
- Sending invoice
- InvoiceDto bilgileri
- UBL XML generated
- InputDocument bilgileri (DocumentUUID, SourceUrn, DestinationUrn)
- Provider response (Code, Explanation)

## 5. Log Seviyeleri

- **INFO**: Genel bilgilendirme logları (fatura oluşturuldu, gönderildi)
- **DEBUG**: Detaylı bilgiler (XML içeriği, InvoiceDto)
- **ERROR**: Hata logları

**Not**: DEBUG loglarını görmek için `application.yml`'de `com.efaturaai.api.invoice: DEBUG` ayarı var.

