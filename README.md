efatura-ai-v2

Hızlı başlangıç ve smoke test komutları.

### Hızlı Başlangıç

- Backend (Java 21 + Spring Boot 3):
```
cd backend
./gradlew clean build
./gradlew itTest
```

- Frontend (React + Vite):
```
cd frontend
npm ci
npm run test
npx playwright install chromium
npm run e2e
```

### Makefile

- Kökten:
```
make unit   # backend unit
make it     # backend integration (Testcontainers)
make fe     # frontend test + e2e
make all    # hepsi
```

### CI

GitHub Actions üç job koşar: `backend-unit`, `backend-it`, `frontend`. Frontend’de Playwright tarayıcıları CLI ile `--with-deps` kurulur.

### Notlar
- Java toolchain 21 zorlanır (Gradle).
- Testcontainers: Postgres 16 ve RabbitMQ otomatik kalkar.
- Playwright: `playwright.config.ts` webServer ile Vite dev server’ı bekler.


