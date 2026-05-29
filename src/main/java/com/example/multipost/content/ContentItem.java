package com.example.multipost.content;

import com.example.multipost.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "contents", indexes = {
        @Index(name = "idx_contents_user_status", columnList = "userId,status")
})
public class ContentItem extends BaseEntity {
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(length = 500)
    private String summary;

    @Lob
    @Column(nullable = false)
    private String body;

    @Column(length = 500)
    private String tags;

    @Column(length = 500)
    private String coverUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContentType contentType = ContentType.ARTICLE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ContentStatus status = ContentStatus.DRAFT;

    @Column(nullable = false)
    private int version = 1;
}
