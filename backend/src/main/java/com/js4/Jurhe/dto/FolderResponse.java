package com.js4.Jurhe.dto;

import java.util.List;

public record FolderResponse(FolderDTO currentFolder, List<FolderDTO> subFolders, List<FileDTO> files) {
}