package com.js4.Jurhe.repo;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.js4.Jurhe.model.FileEntity;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findAllByOwnerId(Long ownderId);
}
