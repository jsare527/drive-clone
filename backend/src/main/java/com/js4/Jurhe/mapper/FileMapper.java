package com.js4.Jurhe.mapper;

import org.springframework.stereotype.Component;

import com.js4.Jurhe.dto.FileDTO;
import com.js4.Jurhe.model.FileEntity;

@Component
public class FileMapper {
    
    public FileDTO toDto(FileEntity file) {
        if (file == null) return null;

        return FileDTO.builder()
            .contentType(file.getContentType())
            .fileName(file.getFileName())
            .id(file.getId())
            .size(file.getSize())
            .uploadTime(file.getUploadTime())
            .build();
    }
}
