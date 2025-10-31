# Git Push Sorunu Çözümü

## Sorun
GitHub'a push yaparken şu hatayı aldık:
```
remote: error: File backend/api/build/libs/api-0.0.1-SNAPSHOT.jar is 100.68 MB; 
this exceeds GitHub's file size limit of 100.00 MB
```

## Çözüm

### 1. Son Commit'i Geri Al
```bash
git reset HEAD~1
```

### 2. Gereksiz Dosyaları Git'ten Kaldır
```bash
# Build dosyalarını kaldır
git rm --cached -r backend/*/build/
git rm --cached -r backend/.gradle/

# node_modules'u kaldır
git rm --cached -r frontend/node_modules/
```

### 3. .gitignore'u Ekle ve Commit Et
```bash
git add .gitignore
git add .github/workflows/
git add backend/Dockerfile
git add backend/api/src/main/resources/application-prod.yml
git add docker-compose.prod.yml
git add docs/
git add scripts/
git add backend/ai-core/src/main/java/com/efaturaai/aicore/FeatureExtractorService.java
git add backend/ai-insights/
git add backend/infra/

# Commit et
git commit -m "feat(stage4): complete production deployment and CI/CD setup

- Add CI/CD workflows with comprehensive tests
- Update Dockerfiles with curl for healthchecks
- Fix environment variable mapping in production config
- Add CI workflow guide documentation
- Fix backend restart issue
- Add .gitignore to exclude build artifacts and node_modules"
```

### 4. Push Et
```bash
git push origin main
```

## Alternatif: BFG Repo-Cleaner (Geçmişten Temizleme)

Eğer commit geçmişinden büyük dosyaları temizlemek isterseniz:

```bash
# BFG Repo-Cleaner indir
# https://rtyley.github.io/bfg-repo-cleaner/

# Büyük dosyaları temizle
java -jar bfg.jar --strip-blobs-bigger-than 50M

# Temizle
git reflog expire --expire=now --all
git gc --prune=now --aggressive
```

## Önemli Notlar

- ✅ Build dosyaları (`build/`, `.gradle/`) git'e eklenmemeli
- ✅ `node_modules/` git'e eklenmemeli
- ✅ JAR dosyaları git'e eklenmemeli
- ✅ `.gitignore` dosyası oluşturuldu ve eklendi

