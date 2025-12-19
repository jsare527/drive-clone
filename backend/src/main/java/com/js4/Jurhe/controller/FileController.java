package com.js4.Jurhe.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.js4.Jurhe.model.FileEntity;
import com.js4.Jurhe.model.User;
import com.js4.Jurhe.service.FileService;
import com.js4.Jurhe.service.UserService;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    private final UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) {
        final User user = getCurrentUser(principal);
        final FileEntity savedFile = fileService.storeFile(file, user.getId());
        return new ResponseEntity<>(savedFile, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FileEntity>> listFiles(Principal principal) {
        final User user = getCurrentUser(principal);
        final List<FileEntity> files = fileService.getAllFilesForUser(user.getId());
        return ResponseEntity.ok(files);
    }

    private User getCurrentUser(Principal principal) {
        final String username = principal.getName();
        return userService.findByUserName(username);
    }

}
