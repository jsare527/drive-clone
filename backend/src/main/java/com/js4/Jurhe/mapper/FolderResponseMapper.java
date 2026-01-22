package com.js4.Jurhe.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.js4.Jurhe.dto.FileDTO;
import com.js4.Jurhe.dto.FolderDTO;
import com.js4.Jurhe.dto.FolderResponse;
import com.js4.Jurhe.model.FileEntity;
import com.js4.Jurhe.model.FolderEntity;

@Component
public class FolderResponseMapper {
    private final FileMapper fileMapper;
    private final FolderMapper folderMapper;

    public FolderResponseMapper(FileMapper fileMapper, FolderMapper folderMapper) {
        this.fileMapper = fileMapper;
        this.folderMapper = folderMapper;
    }

    public FolderResponse toFolderResponse(FolderEntity currentFolder, List<FolderEntity> subFolders, List<FileEntity> files) {
        final FolderDTO currentFolderDTO = folderMapper.toDTO(currentFolder);
        final List<FolderDTO> subFolderDTOs = subFolders.stream().map(folderMapper::toDTO).toList();
        final List<FileDTO> fileDTOs = files.stream().map(fileMapper::toDto).toList();
        return new FolderResponse(currentFolderDTO, subFolderDTOs, fileDTOs);
    }
}
