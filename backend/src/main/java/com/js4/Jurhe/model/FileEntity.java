package com.js4.Jurhe.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@SoftDelete(strategy = SoftDeleteType.TIMESTAMP, columnName = "deleted_at")
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
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    // Timestamp for when the file was first uploaded
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadTime;

    @Column(name = "deleted_at", updatable = false, insertable = false)
    private LocalDateTime deletedAt;

    private boolean isS3 = false;

    @ManyToOne
    @JoinColumn(name = "folder_id", nullable = true)
    @ToString.Exclude
    private FolderEntity folder;
}
