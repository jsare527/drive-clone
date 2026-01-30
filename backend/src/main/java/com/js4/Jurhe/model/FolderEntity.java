package com.js4.Jurhe.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name = "folders")
@SoftDelete(strategy = SoftDeleteType.TIMESTAMP, columnName = "deleted_at")
public class FolderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(name = "deleted_at", updatable = false, insertable = false)
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "parent_folder_id")
    @ToString.Exclude
    private FolderEntity parentFolder;

    @OneToMany(mappedBy = "parentFolder")
    @JsonIgnore
    private List<FolderEntity> subFolders = new ArrayList<>();

    @OneToMany(mappedBy = "folder")
    private List<FileEntity> files = new ArrayList<>();
}
