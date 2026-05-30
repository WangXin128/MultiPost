package com.example.multipost.adapter;

import com.example.multipost.adapter.dto.AdaptContentRequest;
import com.example.multipost.adapter.dto.PlatformContentResponse;
import com.example.multipost.adapter.dto.PlatformContentUpdateRequest;
import com.example.multipost.auth.AuthUserProvider;
import com.example.multipost.content.ContentItem;
import com.example.multipost.content.ContentRepository;
import com.example.multipost.platform.Platform;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformContentService {
    private final ContentRepository contentRepository;
    private final PlatformContentRepository platformContentRepository;
    private final AdapterRegistry adapterRegistry;
    private final AuthUserProvider authUserProvider;

    public PlatformContentService(
            ContentRepository contentRepository,
            PlatformContentRepository platformContentRepository,
            AdapterRegistry adapterRegistry,
            AuthUserProvider authUserProvider) {
        this.contentRepository = contentRepository;
        this.platformContentRepository = platformContentRepository;
        this.adapterRegistry = adapterRegistry;
        this.authUserProvider = authUserProvider;
    }

    @Transactional
    public List<PlatformContentResponse> adapt(Long contentId, AdaptContentRequest request) {
        Long userId = authUserProvider.currentUserId();
        ContentItem content = findOwnedContent(contentId, userId);
        StandardContent standardContent = toStandardContent(content);
        List<Platform> platforms = request == null || request.platforms() == null || request.platforms().isEmpty()
                ? adapterRegistry.supportedPlatforms()
                : request.platforms();

        return platforms.stream()
                .map(platform -> adaptOne(standardContent, platform))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PlatformContentResponse> listByContent(Long contentId) {
        Long userId = authUserProvider.currentUserId();
        findOwnedContent(contentId, userId);
        return platformContentRepository.findByContentIdAndUserIdAndDeletedFalseOrderByPlatformAsc(contentId, userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public PlatformContentResponse update(Long platformContentId, PlatformContentUpdateRequest request) {
        PlatformContent platformContent = platformContentRepository
                .findByIdAndUserIdAndDeletedFalse(platformContentId, authUserProvider.currentUserId())
                .orElseThrow(() -> new EntityNotFoundException("platform content not found"));
        platformContent.setTitle(request.title());
        platformContent.setSummary(request.summary());
        platformContent.setBody(request.body());
        platformContent.setTags(joinTags(request.tags()));
        return toResponse(platformContent);
    }

    private PlatformContent adaptOne(StandardContent content, Platform platform) {
        PlatformAdapter adapter = adapterRegistry.get(platform);
        AdaptedContent adapted = adapter.adapt(content);
        PlatformValidationResult validation = adapter.validate(adapted);
        if (!validation.valid()) {
            throw new IllegalArgumentException(platform + " adapted content invalid: " + validation.errors());
        }

        PlatformContent entity = platformContentRepository
                .findByContentIdAndPlatformAndSourceVersionAndUserIdAndDeletedFalse(
                        content.contentId(), platform, content.sourceVersion(), content.userId())
                .orElseGet(PlatformContent::new);
        entity.setUserId(content.userId());
        entity.setContentId(content.contentId());
        entity.setPlatform(platform);
        entity.setTitle(adapted.title());
        entity.setSummary(adapted.summary());
        entity.setBody(adapted.body());
        entity.setTags(joinTags(adapted.tags()));
        entity.setSourceVersion(content.sourceVersion());
        return platformContentRepository.save(entity);
    }

    private ContentItem findOwnedContent(Long contentId, Long userId) {
        return contentRepository.findByIdAndUserIdAndDeletedFalse(contentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("content not found"));
    }

    private StandardContent toStandardContent(ContentItem content) {
        return new StandardContent(
                content.getId(),
                content.getUserId(),
                content.getTitle(),
                content.getSummary(),
                content.getBody(),
                splitTags(content.getTags()),
                content.getCoverUrl(),
                content.getContentType(),
                content.getVersion());
    }

    private PlatformContentResponse toResponse(PlatformContent entity) {
        return new PlatformContentResponse(
                entity.getId(),
                entity.getContentId(),
                entity.getPlatform(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getBody(),
                splitTags(entity.getTags()),
                entity.getSourceVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tags.split("[,，、\\s]+"))
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .toList();
    }

    private String joinTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return String.join(",", tags.stream()
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .toList());
    }
}
