package com.js4.Jurhe.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileDTO {
    private Long id;
    private String fileName;
    private String contentType;
    private Long size;
    private LocalDateTime uploadTime;
}
