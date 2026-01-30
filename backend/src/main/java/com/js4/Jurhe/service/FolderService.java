package com.js4.Jurhe.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.js4.Jurhe.dto.FolderDTO;
import com.js4.Jurhe.dto.FolderResponse;
import com.js4.Jurhe.dto.TrashDTO;
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
    private final FileService fileService;
    private final StorageService storageService;

    public FolderService(FolderRepository folderRepository, UserService userService, FolderMapper folderMapper, 
        FileRepository fileRepository, FolderResponseMapper folderResponseMapper, FileService fileService,
        StorageService storageService
    ) {
        this.folderRepository = folderRepository;
        this.userService = userService;
        this.fileRepository = fileRepository;
        this.folderMapper = folderMapper;
        this.folderResponseMapper = folderResponseMapper;
        this.fileService = fileService;
        this.storageService = storageService;
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

    public int createLinkedFolders(Long folderId, String[] relativePaths, MultipartFile[] files, Long userId) {
        int foldersCreated = 1;
        final Map<String, FolderDTO> map = new HashMap<>();
        final String initialName = relativePaths[0].split("/")[0];
        final FolderDTO parent = folderId == 0 ? createFolder(initialName, userId, null) 
                                               : createFolder(initialName, userId, folderId);
        map.put(initialName, parent);

        for (int i = 0; i < relativePaths.length; i++) {
            final String[] splitPath = relativePaths[i].split("/");

            for (int j = 1; j < splitPath.length - 1; j++) {
                final String currentToken = splitPath[j];
                if (!map.containsKey(currentToken)) {
                    final FolderDTO prev = map.get(splitPath[j - 1]);
                    final FolderDTO child = createFolder(currentToken, userId, prev.getId());
                    map.put(currentToken, child);
                    foldersCreated++;
                }
            }

            final long folderIdStore = map.get(splitPath[splitPath.length - 2]).getId();
            fileService.storeFile(files[i], userId, folderIdStore);
        }
        return foldersCreated;
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

    public FolderResponse getTrashContents(Long userId) {
        final List<FolderEntity> subFolders = folderRepository.findTrashFolders(userId);
        System.out.println(subFolders);
        final List<FileEntity> files = fileRepository.findTrashFiles(userId);
        return folderResponseMapper.toFolderResponse(null, subFolders, files);
    }

    public Page<TrashDTO> getTrashPaginated(Long userId, Pageable pageable) {
        final List<FolderEntity> subFolders = folderRepository.findTrashFolders(userId);
        final List<FileEntity> files = fileRepository.findTrashFiles(userId);
        final List<TrashDTO> allTrash = new ArrayList<>();

        subFolders.forEach(fo -> {
            final String originalPath = fo.getParentFolder() != null ? fo.getParentFolder().getName() : "Root";
            allTrash.add(new TrashDTO(
                fo.getId(),
                fo.getName(),
                "folder",
                fo.getDeletedAt(),
                originalPath
            ));
        });

        files.forEach(fi -> {
            final String originalPath = fi.getFolder() != null ? fi.getFolder().getName() : "Root";
            allTrash.add(new TrashDTO(
                fi.getId(),
                fi.getFileName(),
                "file",
                fi.getDeletedAt(),
                originalPath
            ));
        });
        allTrash.sort(Comparator.comparing(TrashDTO::deletedAt, Comparator.nullsLast(Comparator.reverseOrder())));

        List<TrashDTO> pageContent = allTrash.stream()
        .skip((long) pageable.getPageNumber() * pageable.getPageSize())
        .limit(pageable.getPageSize())
        .toList();
        
        return new PageImpl<>(pageContent, pageable, allTrash.size());
    }

    public boolean deleteFolder(Long folderId) {
        try {
            folderRepository.deleteById(folderId);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public void addFolderToZip(Long folderId, String path, ZipOutputStream zStream) throws IOException {
        FolderEntity folder = folderRepository.findById(folderId).orElseThrow();
        String currPath = path + folder.getName() + "/";

        zStream.putNextEntry(new ZipEntry(currPath));
        zStream.closeEntry();

        for (FileEntity file : folder.getFiles()) {
            ZipEntry entry = new ZipEntry(currPath + file.getFileName());
            zStream.putNextEntry(entry);
            final byte[] data = storageService.loadAsResource(file.getStoragePath()).getContentAsByteArray();
            zStream.write(data);
            zStream.closeEntry();
        }

        for (FolderEntity subFolder : folder.getSubFolders()) {
            addFolderToZip(subFolder.getId(), currPath, zStream);
        }
    }

    public FolderDTO getFolder(Long folderId) {
        FolderEntity entity = folderRepository.findById(folderId).orElseThrow();
        return folderMapper.toDTO(entity);
    }

    @Transactional
    public void delete(Long folderId) {
        final FolderEntity parent = folderRepository.findById(folderId)
        .orElseThrow(() -> new RuntimeException("parent folder not found"));

        // deletes all files within folder
        fileRepository.softDeleteByFolderId(folderId);

        for (FolderEntity subFolder : parent.getSubFolders()) {
            delete(subFolder.getId());
        }
        
        folderRepository.deleteById(folderId);
    }
}
