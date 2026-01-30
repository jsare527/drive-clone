package com.js4.Jurhe.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.js4.Jurhe.dto.FileDTO;
import com.js4.Jurhe.model.FileEntity;
import com.js4.Jurhe.model.User;
import com.js4.Jurhe.service.FileService;
import com.js4.Jurhe.service.StorageService;
import com.js4.Jurhe.service.UserService;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    private final UserService userService;
    private final StorageService storageService;

    public FileController(FileService fileService, UserService userService, StorageService storageService) {
        this.fileService = fileService;
        this.userService = userService;
        this.storageService = storageService;
    }

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(required = false) Long folderId, Principal principal) {
        final User user = userService.getCurrentUser(principal);
        final FileDTO savedFile = fileService.storeFile(file, user.getId(), folderId);
        return new ResponseEntity<>(savedFile, HttpStatus.CREATED);
    }

    @PostMapping(value = "/uploadFiles")
    public ResponseEntity<?> uploadFiles(@RequestParam("folderId") Long folderId, @RequestParam("files") MultipartFile[] files, Principal principal) {
        final User user = userService.getCurrentUser(principal);
        final int fileCount = fileService.storeFiles(files, user.getId(), folderId);
        return ResponseEntity.ok(fileCount);
    }

    @GetMapping(value = "/downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileId") Long fileId) throws IOException {
        final FileEntity fileEntity = fileService.getFile(fileId);
        final Resource resource = storageService.loadAsResource(fileEntity.getStoragePath());
        return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(fileEntity.getContentType()))
        .contentLength(resource.contentLength())
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getFileName() + "\"")
        .body(resource);
    }

    @PostMapping(value = "/deleteFile")
    public ResponseEntity<?> deleteFile(@RequestParam("fileId") Long fileId) {
        if (fileId != 0 && fileService.deleteFile(fileId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.internalServerError().build();
    }
}
