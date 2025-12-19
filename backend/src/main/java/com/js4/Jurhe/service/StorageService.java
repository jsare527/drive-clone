package com.js4.Jurhe.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface StorageService {
    String store(MultipartFile file, Long ownerId);
    Resource loadAsResource(String storagePath);
    void delete(String storagePath);
}
