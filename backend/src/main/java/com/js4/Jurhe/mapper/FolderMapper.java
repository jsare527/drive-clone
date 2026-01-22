package com.js4.Jurhe.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.js4.Jurhe.dto.FolderDTO;
import com.js4.Jurhe.model.FolderEntity;

@Component
public class FolderMapper {
    
    public FolderDTO toDTO(FolderEntity entity) {
        if (entity == null) return null;

        return FolderDTO.builder()
        .id(entity.getId())
        .name(entity.getName())
        .parentId(entity.getParentFolder() != null ? entity.getParentFolder().getId() : null)
        .fileCount(entity.getFiles() != null ? entity.getFiles().size() : 0)
        .build();
    }

    public List<FolderDTO> toDTOList(List<FolderEntity> entities) {
        return entities.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
    }
}
