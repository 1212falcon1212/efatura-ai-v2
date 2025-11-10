package com.efaturaai.core.storage;

import java.util.Map;
import java.util.UUID;

public interface StorageService {
  String store(
      UUID tenantId,
      String type,
      int year,
      String objectName,
      byte[] data,
      String contentType,
      Map<String, String> userMetadata);
}


