package com.js4.Jurhe.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.js4.Jurhe.model.FileEntity;
import com.js4.Jurhe.repo.FileRepository;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final StorageService storageService;

    public FileService(FileRepository fileRepository, StorageService storageService) {
        this.fileRepository = fileRepository;
        this.storageService = storageService;
    }

    public FileEntity storeFile(MultipartFile file, Long ownerId) {
        String storagePath = storageService.store(file, ownerId);

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setContentType(file.getContentType());
        fileEntity.setSize(file.getSize());
        fileEntity.setStoragePath(storagePath);
        fileEntity.setOwnerId(ownerId);

        return fileRepository.save(fileEntity);
    }

    public List<FileEntity> getAllFilesForUser(Long ownerId) {
        return fileRepository.findAllByOwnerId(ownerId);
    }
}
