package com.js4.Jurhe.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.js4.Jurhe.model.FolderEntity;

public interface FolderRepository extends JpaRepository<FolderEntity, Long> {
    List<FolderEntity> findByUserIdAndParentFolderIsNull(Long userId);
    List<FolderEntity> findByParentFolderId(Long folderId);
}
