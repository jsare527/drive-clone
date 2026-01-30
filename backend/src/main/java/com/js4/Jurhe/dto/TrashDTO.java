package com.js4.Jurhe.dto;

import java.time.LocalDateTime;

public record TrashDTO(Long id, String name, String type, LocalDateTime deletedAt, String originalPath) {}
