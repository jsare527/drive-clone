package com.js4.Jurhe.repo;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.js4.Jurhe.model.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByFolderId(Long folderId);
    List<FileEntity> findByUserIdAndFolderIsNull(Long userId);

    @Query(value = "SELECT fi.* FROM files fi LEFT JOIN folders fo ON fi.folder_id = fo.id WHERE fi.user_id = :userId AND fi.deleted_at IS NOT NULL AND (fi.folder_id IS NULL OR fo.deleted_at IS NULL)", nativeQuery = true)
    List<FileEntity> findTrashFiles(Long userId);

    @Modifying
    @Query(value = "UPDATE files SET deleted_at = CURRENT_TIMESTAMP WHERE folder_id = :folderId", nativeQuery = true)
    void softDeleteByFolderId(@Param("folderId") Long folderId);
}
