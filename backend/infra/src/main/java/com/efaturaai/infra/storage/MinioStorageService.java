package com.efaturaai.infra.storage;

import com.efaturaai.core.storage.StorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
// lifecycle/versioning ops intentionally omitted for compatibility
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Year;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MinioStorageService implements StorageService {
  private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);

  private final MinioClient client;
  private final String bucket;
  private final int retentionDays;
  private final boolean objectLockEnabled;

  public MinioStorageService(
      @Value("${storage.minio.endpoint:http://localhost:9000}") String endpoint,
      @Value("${storage.minio.accessKey:minioadmin}") String accessKey,
      @Value("${storage.minio.secretKey:minioadmin}") String secretKey,
      @Value("${storage.minio.bucket:efatura}") String bucket,
      @Value("${storage.minio.retentionDays:1825}") int retentionDays,
      @Value("${storage.minio.objectLockEnabled:false}") boolean objectLockEnabled) {
    this.client =
        MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
    this.bucket = bucket;
    this.retentionDays = retentionDays;
    this.objectLockEnabled = objectLockEnabled;
    ensureBucket();
  }

  @Override
  public String store(
      UUID tenantId,
      String type,
      int year,
      String objectName,
      byte[] data,
      String contentType,
      Map<String, String> userMetadata) {
    String key = buildKey(tenantId, type, year, objectName);
    Map<String, String> meta = new HashMap<>();
    if (userMetadata != null) meta.putAll(userMetadata);
    meta.put("sha256", sha256Base64(data));
    meta.put("contentType", contentType);
    try {
      client.putObject(
          PutObjectArgs.builder()
              .bucket(bucket)
              .object(key)
              .contentType(contentType)
              .userMetadata(meta)
              .stream(new ByteArrayInputStream(data), data.length, -1)
              .build());
      log.info("Stored object: bucket={}, key={}", bucket, key);
      return key;
    } catch (Exception e) {
      throw new RuntimeException("Failed to store object to MinIO", e);
    }
  }

  private String buildKey(UUID tenantId, String type, int year, String objectName) {
    return tenantId + "/" + type + "/" + year + "/" + objectName;
  }

  private String sha256Base64(byte[] data) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      return Base64.getEncoder().encodeToString(md.digest(data));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void ensureBucket() {
    try {
      boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
      if (!exists) {
        client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
      }
      // Lifecycle setup skipped in this build for portability
    } catch (Exception e) {
      // do not fail startup on bucket errors; log only
      log.warn("MinIO bucket check failed: {}", e.getMessage());
    }
  }
}


