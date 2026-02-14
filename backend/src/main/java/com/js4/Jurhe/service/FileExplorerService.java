package com.js4.Jurhe.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.js4.Jurhe.dto.FolderResponse;
import com.js4.Jurhe.mapper.FolderResponseMapper;
import com.js4.Jurhe.model.FileEntity;
import com.js4.Jurhe.model.FolderEntity;
import com.js4.Jurhe.repo.FileRepository;
import com.js4.Jurhe.repo.FolderRepository;

@Service
public class FileExplorerService {
    private final FileRepository fileRepository;
    private final FolderRepository folderRepository;
    private final FolderResponseMapper folderResponseMapper;
    
    public FileExplorerService(FileRepository fileRepository, FolderRepository folderRepository, FolderResponseMapper folderResponseMapper) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.folderResponseMapper = folderResponseMapper;
    }


    public FolderResponse search(Long userId, Long currentFolderId, String query) {
        FolderEntity currentFolder = null;
        List<FileEntity> files;
        List<FolderEntity> folders;

        if (currentFolderId == null) {
            files = fileRepository.rootSearchFiles(userId, query);
            folders = folderRepository.rootSearchFolders(userId, query);
        } else {
            currentFolder = folderRepository.findById(currentFolderId).orElse(null);
            files = fileRepository.searchFilesByFolder(userId, currentFolderId, query);
            folders = folderRepository.searchSubFolders(userId, currentFolderId, query);
        }
        
        return folderResponseMapper.toFolderResponse(currentFolder, folders, files);
    }

    public String resolveTrashFolderPath(FolderEntity folderEntity, boolean isFile) {
        FolderEntity current = folderEntity;
        final StringBuilder sb = new StringBuilder("../");
        final List<String> folderNames = new ArrayList<>();
        if (isFile) folderNames.add(current.getName());

        while (current.getParentFolder() != null) {
            current = current.getParentFolder();
            folderNames.add(current.getName());
        }

        sb.append(String.join("/", folderNames.reversed()));
        return sb.toString();
    }
}
