package com.js4.Jurhe.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.js4.Jurhe.dto.FileDTO;
import com.js4.Jurhe.mapper.FileMapper;
import com.js4.Jurhe.model.FileEntity;
import com.js4.Jurhe.model.FolderEntity;
import com.js4.Jurhe.model.User;
import com.js4.Jurhe.repo.FileRepository;
import com.js4.Jurhe.repo.FolderRepository;

@Service
public class FileService {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final StorageService storageService;
    private final UserService userService;
    private final FileMapper fileMapper;

    public FileService(FileRepository fileRepository, StorageService storageService, UserService userService, FolderRepository folderRepository, FileMapper fileMapper) {
        this.fileRepository = fileRepository;
        this.storageService = storageService;
        this.userService = userService;
        this.folderRepository = folderRepository;
        this.fileMapper = fileMapper;
    }

    public FileDTO storeFile(MultipartFile file, Long ownerId, Long folderId) {
        final String storagePath = storageService.store(file);
        final User user = userService.findById(ownerId);

        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setContentType(file.getContentType());
        fileEntity.setSize(file.getSize());
        fileEntity.setStoragePath(storagePath);
        fileEntity.setUser(user);

        if (folderId != null) {
            final FolderEntity parent = folderRepository.findById(folderId)
            .orElseThrow(() -> new RuntimeException("Folder not found for id: " + folderId));
            fileEntity.setFolder(parent);
        } else {
            fileEntity.setFolder(null);
        }

        final FileEntity savedFile = fileRepository.save(fileEntity);
        return fileMapper.toDto(savedFile);
    }

    public int storeFiles(MultipartFile[] files, Long userId, Long folderId) {
        final List<FileEntity> fileEntities = new ArrayList<>();
        final User user = userService.findById(userId);
        FolderEntity parent = null;

        if (folderId != 0) {
            parent = folderRepository.findById(folderId)
            .orElseThrow(() -> new RuntimeException("Parent folder not found"));
        } 

        for (final MultipartFile file : files) {
            FileEntity fileEntity = new FileEntity();

            final String storagePath = storageService.store(file);
            fileEntity.setFileName(file.getOriginalFilename());
            fileEntity.setContentType(file.getContentType());
            fileEntity.setSize(file.getSize());
            fileEntity.setStoragePath(storagePath);
            fileEntity.setUser(user);
            fileEntity.setFolder(parent);
            fileEntities.add(fileEntity);
        }

        fileRepository.saveAll(fileEntities);
        return fileEntities.size();
    }

    public FileEntity getFile(Long fileId) {
        return fileRepository.findById(fileId).orElseThrow();
    }

    public boolean deleteFile(Long fileId) {
        try {
            fileRepository.deleteById(fileId);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
