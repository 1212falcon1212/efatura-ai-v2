Smoke Tests

### Backend

- Unit:
```
cd backend
./gradlew clean build -x itTest
```

- Integration (Testcontainers + WireMock):
```
./gradlew itTest
```

Doğrulamalar: `/actuator/health`, `/auth/login`, fatura oluştur → imzala (MockSigner) → gönder (stub provider), PDF endpoint, outbox → RabbitMQ tüketimi.

### Frontend

- Unit (Vitest):
```
cd frontend
npm ci
npm run test
```

- E2E (Playwright):
```
npx playwright install chromium
npm run e2e
```

Playwright `webServer` ayarı Vite dev server’ı başlatıp `http://localhost:5173`’e karşı smoke yürütür.

### CI

Workflow job’ları:
- backend-unit → Gradle unit
- backend-it → Gradle integration
- frontend → npm ci → vitest → playwright (`npx playwright install --with-deps chromium`)


