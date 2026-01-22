package com.js4.Jurhe.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.js4.Jurhe.dto.FolderDTO;
import com.js4.Jurhe.dto.FolderResponse;
import com.js4.Jurhe.mapper.FolderMapper;
import com.js4.Jurhe.mapper.FolderResponseMapper;
import com.js4.Jurhe.model.FileEntity;
import com.js4.Jurhe.model.FolderEntity;
import com.js4.Jurhe.model.User;
import com.js4.Jurhe.repo.FileRepository;
import com.js4.Jurhe.repo.FolderRepository;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final FolderResponseMapper folderResponseMapper;
    private final UserService userService;
    private final FolderMapper folderMapper;

    public FolderService(FolderRepository folderRepository, UserService userService, FolderMapper folderMapper, FileRepository fileRepository, FolderResponseMapper folderResponseMapper) {
        this.folderRepository = folderRepository;
        this.userService = userService;
        this.fileRepository = fileRepository;
        this.folderMapper = folderMapper;
        this.folderResponseMapper = folderResponseMapper;
    }


    public FolderDTO createFolder(String name, Long userId, Long parentFolderId) {
        final User user = userService.findById(userId);
        FolderEntity newFolder = new FolderEntity();
        newFolder.setName(name);
        newFolder.setUser(user);

        if (parentFolderId != null) {
            final FolderEntity parent = folderRepository.findById(parentFolderId)
            .orElseThrow(() -> new RuntimeException("Parent folder not found for id: " + parentFolderId));
            newFolder.setParentFolder(parent);
        } else {
            newFolder.setParentFolder(null);
        }

        final FolderEntity folder = folderRepository.save(newFolder);
        return folderMapper.toDTO(folder);
    }

    public FolderResponse getFolderContents(Long userId, Long folderId) {
        List<FolderEntity> subfolders;
        List<FileEntity> files;
        FolderEntity currentFolder = null;

        if (folderId == null) {
            subfolders = folderRepository.findByUserIdAndParentFolderIsNull(userId);
            files = fileRepository.findByUserIdAndFolderIsNull(userId);
        } else {
            currentFolder = folderRepository.findById(folderId)
                            .orElseThrow(() -> new RuntimeException("Current folder not found"));
            subfolders = folderRepository.findByParentFolderId(folderId);
            files = fileRepository.findByFolderId(folderId);
        }

        return folderResponseMapper.toFolderResponse(currentFolder, subfolders, files);
    }
}
