package com.js4.Jurhe.repo;
import java.util.List;
import java.util.Optional;

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

    @Query(value = "SELECT fi.* FROM files fi WHERE fi.user_id = :userId AND fi.id = :fileId", nativeQuery = true)
    Optional<FileEntity> findTrashFileById(@Param("fileId") Long fileId, @Param("userId") Long userId);

    @Modifying
    @Query(value = "UPDATE files SET deleted_at = CURRENT_TIMESTAMP WHERE folder_id = :folderId", nativeQuery = true)
    void softDeleteByFolderId(@Param("folderId") Long folderId);

    @Modifying
    @Query(value = "DELETE FROM files WHERE folder_id = :folderId", nativeQuery = true)
    void deleteForeverByFolderId(@Param("folderId") Long folderId);

    @Modifying
    @Query(value = "DELETE FROM files WHERE id = :fileId", nativeQuery = true)
    void deleteFileForeverById(@Param("fileId") Long fileId);

    @Query("SELECT f FROM FileEntity f WHERE f.user.id = :userId AND f.folder IS NULL AND lower(f.fileName) LIKE lower(concat('%', :searchTerm, '%'))")
    List<FileEntity> rootSearchFiles(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);

    @Query("SELECT f FROM FileEntity f WHERE f.user.id = :userId AND f.folder.id = :folderId AND lower(f.fileName) LIKE lower(concat('%', :searchTerm, '%'))")
    List<FileEntity> searchFilesByFolder(@Param("userId") Long userId, @Param("folderId") Long folderId, @Param("searchTerm") String searchTerm);

    @Modifying
    @Query(value = "UPDATE files SET deleted_at = NULL WHERE folder_id = :folderId", nativeQuery = true)
    void restoreByFolderId(@Param("folderId") Long folderId);

    @Modifying
    @Query(value = "UPDATE files SET deleted_at = NULL WHERE folder_id IN :ids", nativeQuery = true)
    void bulkRestoreFiles(@Param("ids") List<Long> ids);

    @Modifying
    @Query(value = "UPDATE files SET deleted_at = NULL WHERE id = :fileId", nativeQuery = true)
    void restoreFileById(@Param("fileId") Long fileId);

    @Query(value = "SELECT * FROM files WHERE folder_id IN :ids", nativeQuery = true)
    List<FileEntity> findSubFolderFiles(@Param("ids") List<Long> ids);
}
