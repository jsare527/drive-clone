package com.js4.Jurhe.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.js4.Jurhe.model.FolderEntity;

public interface FolderRepository extends JpaRepository<FolderEntity, Long> {
    List<FolderEntity> findByUserIdAndParentFolderIsNull(Long userId);
    List<FolderEntity> findByParentFolderId(Long folderId);

    @Query(value = "SELECT f1.* FROM folders f1 WHERE f1.user_id = :userId AND f1.deleted_at IS NOT NULL AND (f1.parent_folder_id IS NULL OR (SELECT f2.deleted_at FROM folders f2 WHERE f2.id = f1.parent_folder_id) IS NULL)", nativeQuery = true)
    List<FolderEntity> findTrashFolders(@Param("userId") Long userId);

    @Query(value = "SELECT f1.* FROM folders f1 WHERE f1.user_id = :userId " +
            "AND f1.deleted_at IS NOT NULL " +
            "AND (f1.parent_folder_id IS NULL OR (SELECT f2.deleted_at FROM folders f2 WHERE f2.id = folders.parent_folder_id) IS NULL)",
    countQuery = "SELECT count(f1.*) FROM folders f1 WHERE f1.user_id = :userId AND f1.deleted_at IS NOT NULL " +
                "AND (f1.parent_folder_id IS NULL OR (SELECT f2.deleted_at FROM folders f2 WHERE f2.id = folders.parent_folder_id) IS NULL)",
    nativeQuery = true)
    Page<FolderEntity> getTrashFoldersPaginated(@Param("userId") Long userId, Pageable pageable);
}
