package com.js4.Jurhe.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.zip.ZipOutputStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.js4.Jurhe.dto.FolderDTO;
import com.js4.Jurhe.dto.FolderResponse;
import com.js4.Jurhe.dto.TrashDTO;
import com.js4.Jurhe.model.User;
import com.js4.Jurhe.service.FolderService;
import com.js4.Jurhe.service.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/folders")
public class FolderController {
    private final FolderService folderService;
    private final UserService userService;
    

    public FolderController(FolderService folderService, UserService userService) {
        this.folderService = folderService;
        this.userService = userService;
    }

    @PostMapping(value = "/createFolder")
    public ResponseEntity<FolderDTO> createFolder(@RequestParam String name, @RequestParam(required = false) Long folderId, Principal principal) {
        final User user = userService.getCurrentUser(principal);
        final FolderDTO folder = folderService.createFolder(name, user.getId(), folderId);
        return ResponseEntity.ok(folder);
    }

    @PostMapping(value = "/uploadFolder")
    public ResponseEntity<?> uploadFolder(@RequestParam("folderId") Long folderId, @RequestParam("relativePaths") String[] relativePaths,
                                          @RequestParam("files") MultipartFile[] files, Principal principal) {
        final User user = userService.getCurrentUser(principal);
        final int foldersCreated = folderService.createLinkedFolders(folderId, relativePaths, files, user.getId());
        return ResponseEntity.ok(foldersCreated);
    }

    @GetMapping(value = "/downloadFolder")
    public void downloadFolder(@RequestParam("folderId") Long folderId, HttpServletResponse response) throws IOException {
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"folder_download.zip\"");

        try (BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            ZipOutputStream zos = new ZipOutputStream(bos)) {
            folderService.addFolderToZip(folderId, "", zos);
            zos.finish();
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/deleteFolder")
    public ResponseEntity<?> deleteFolder(@RequestParam("folderId") Long folderId) {
        try {
            folderService.delete(folderId);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @GetMapping(value = "/trashTest")
    public ResponseEntity<FolderResponse> getTrashContents(Principal principal) {
        final User user = userService.getCurrentUser(principal);
        final FolderResponse response = folderService.getTrashContents(user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trash")
    public ResponseEntity<Page<TrashDTO>> getTrash(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, Principal principal) {
        final User user = userService.getCurrentUser(principal);
        Pageable pageable = PageRequest.of(page, size);
        Page<TrashDTO> trashPage = folderService.getTrashPaginated(user.getId(), pageable);
        return ResponseEntity.ok(trashPage);
    }

    @GetMapping
    public ResponseEntity<FolderResponse> getContents(@RequestParam(required = false) Long folderId, Principal principal) {
        final User user = userService.getCurrentUser(principal);
        final FolderResponse response = folderService.getFolderContents(user.getId(), folderId);
        return ResponseEntity.ok(response);
    }
}
