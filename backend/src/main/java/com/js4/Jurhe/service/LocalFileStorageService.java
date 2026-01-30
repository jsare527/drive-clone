package com.js4.Jurhe.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "local")
public class LocalFileStorageService implements StorageService {

    @Value("${file.upload.dir}")
    private String uploadDir;
    private final Path rootLocation;
    private final ResourceLoader resourceLoader;

    public LocalFileStorageService(@Value("${file.upload.dir}") String uploadDir, ResourceLoader resourceLoader) throws IOException {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.rootLocation);
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String store(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String storageFileName = UUID.randomUUID().toString() + "-" + originalFileName;

        try {
            Path targetLocation = this.rootLocation.resolve(storageFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName, ex);
        }
    }

    @Override
    public Resource loadAsResource(String storagePath) {
        return resourceLoader.getResource("file:" + storagePath);
    }

    @Override
    public void delete(String storagePath) {
        try {
            Files.deleteIfExists(this.rootLocation.resolve(storagePath));
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file: " + storagePath, ex);
        }
    }
    
}
