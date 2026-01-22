package com.js4.Jurhe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FolderDTO {
   private Long id;
   private String name;
   private Long parentId;
   private int fileCount;
}
