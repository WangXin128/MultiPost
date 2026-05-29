package com.example.multipost.content;

import com.example.multipost.auth.AuthUserProvider;
import com.example.multipost.content.dto.ContentCreateRequest;
import com.example.multipost.content.dto.ContentResponse;
import com.example.multipost.content.dto.ContentUpdateRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentService {
    private final ContentRepository contentRepository;
    private final AuthUserProvider authUserProvider;

    public ContentService(ContentRepository contentRepository, AuthUserProvider authUserProvider) {
        this.contentRepository = contentRepository;
        this.authUserProvider = authUserProvider;
    }

    @Transactional
    public ContentResponse create(ContentCreateRequest request) {
        ContentItem item = new ContentItem();
        item.setUserId(authUserProvider.currentUserId());
        item.setTitle(request.title());
        item.setSummary(request.summary());
        item.setBody(request.body());
        item.setTags(request.tags());
        item.setCoverUrl(request.coverUrl());
        item.setContentType(request.contentType());
        item.setStatus(request.status() == null ? ContentStatus.DRAFT : request.status());
        return toResponse(contentRepository.save(item));
    }

    @Transactional(readOnly = true)
    public ContentResponse get(Long id) {
        return toResponse(findOwned(id));
    }

    @Transactional(readOnly = true)
    public Page<ContentResponse> list(Pageable pageable) {
        return contentRepository.findByUserIdAndDeletedFalse(authUserProvider.currentUserId(), pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ContentResponse update(Long id, ContentUpdateRequest request) {
        ContentItem item = findOwned(id);
        item.setTitle(request.title());
        item.setSummary(request.summary());
        item.setBody(request.body());
        item.setTags(request.tags());
        item.setCoverUrl(request.coverUrl());
        item.setContentType(request.contentType());
        item.setStatus(request.status());
        item.setVersion(item.getVersion() + 1);
        return toResponse(item);
    }

    @Transactional
    public void delete(Long id) {
        ContentItem item = findOwned(id);
        item.setDeleted(true);
    }

    private ContentItem findOwned(Long id) {
        return contentRepository.findByIdAndUserIdAndDeletedFalse(id, authUserProvider.currentUserId())
                .orElseThrow(() -> new EntityNotFoundException("content not found"));
    }

    private ContentResponse toResponse(ContentItem item) {
        return new ContentResponse(
                item.getId(),
                item.getTitle(),
                item.getSummary(),
                item.getBody(),
                item.getTags(),
                item.getCoverUrl(),
                item.getContentType(),
                item.getStatus(),
                item.getVersion(),
                item.getCreatedAt(),
                item.getUpdatedAt());
    }
}
