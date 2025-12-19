package com.js4.Jurhe.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    // The type of the file (e.g., "application/pdf", "image/jpeg")
    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private Long size;

    // The full path where the file is physically stored on the server's file system
    @Column(nullable = false)
    private String storagePath;

    // NOTE: This will be linked to a User entity later via a @ManyToOne relationship
    @Column(nullable = false)
    private Long ownerId;

    // Timestamp for when the file was first uploaded
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadTime;

    // Optional: Could be used for features like "deleted" or "shared" status
    private boolean isDeleted = false;

    private boolean isS3 = false;

    // Optional: For handling file organization (folders)
    // For now, assume null means root directory. Later, link to a FolderEntity.
    private Long parentFolderId;
}
