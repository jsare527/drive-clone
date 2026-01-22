package com.js4.Jurhe.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

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
import com.js4.Jurhe.dto.FolderDTO;
import com.js4.Jurhe.dto.FolderResponse;
import com.js4.Jurhe.model.User;
import com.js4.Jurhe.service.FileService;
import com.js4.Jurhe.service.FolderService;
import com.js4.Jurhe.service.UserService;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    private final UserService userService;
    private final FolderService folderService;

    public FileController(FileService fileService, UserService userService, FolderService folderService) {
        this.fileService = fileService;
        this.userService = userService;
        this.folderService = folderService;
    }

    @PostMapping(value = "/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(required = false) Long folderId, Principal principal) {
        final User user = userService.getCurrentUser(principal);
        final FileDTO savedFile = fileService.storeFile(file, user.getId(), folderId);
        return new ResponseEntity<>(savedFile, HttpStatus.CREATED);
    }

    @PostMapping(value = "/createFolder")
    public ResponseEntity<FolderDTO> createFolder(@RequestParam String name, @RequestParam(required = false) Long folderId, Principal principal) {
        final User user = userService.getCurrentUser(principal);
        final FolderDTO folder = folderService.createFolder(name, user.getId(), folderId);
        return ResponseEntity.ok(folder);
    }


    @GetMapping
    public ResponseEntity<FolderResponse> getContents(@RequestParam(required = false) Long folderId, Principal principal) {
        final User user = userService.getCurrentUser(principal);
        final FolderResponse response = folderService.getFolderContents(user.getId(), folderId);
        return ResponseEntity.ok(response);
    }
}
