package com.example.multipost.adapter;

import com.example.multipost.common.BaseEntity;
import com.example.multipost.platform.Platform;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "platform_contents",
        indexes = @Index(name = "idx_platform_contents_content", columnList = "contentId"),
        uniqueConstraints = @UniqueConstraint(name = "uk_platform_content_version",
                columnNames = {"contentId", "platform", "sourceVersion"}))
public class PlatformContent extends BaseEntity {
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long contentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Platform platform;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 500)
    private String summary;

    @Lob
    @Column(nullable = false)
    private String body;

    @Column(length = 500)
    private String tags;

    @Column(nullable = false)
    private int sourceVersion;
}
