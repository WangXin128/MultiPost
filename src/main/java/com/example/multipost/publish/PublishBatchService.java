package com.example.multipost.publish;

import com.example.multipost.adapter.PlatformContent;
import com.example.multipost.adapter.PlatformContentRepository;
import com.example.multipost.auth.AuthUserProvider;
import com.example.multipost.content.ContentItem;
import com.example.multipost.content.ContentRepository;
import com.example.multipost.platform.Platform;
import com.example.multipost.publish.dto.PublishBatchCreateRequest;
import com.example.multipost.publish.dto.PublishBatchResponse;
import com.example.multipost.publish.dto.PublishTaskResponse;
import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class PublishBatchService {
    private final PublishBatchRepository publishBatchRepository;
    private final PublishTaskRepository publishTaskRepository;
    private final ContentRepository contentRepository;
    private final PlatformContentRepository platformContentRepository;
    private final AuthUserProvider authUserProvider;
    private final IdempotencyService idempotencyService;
    private final PublishOutboxService publishOutboxService;
    private final PublishOutboxProcessor publishOutboxProcessor;

    public PublishBatchService(
            PublishBatchRepository publishBatchRepository,
            PublishTaskRepository publishTaskRepository,
            ContentRepository contentRepository,
            PlatformContentRepository platformContentRepository,
            AuthUserProvider authUserProvider,
            IdempotencyService idempotencyService,
            PublishOutboxService publishOutboxService,
            PublishOutboxProcessor publishOutboxProcessor) {
        this.publishBatchRepository = publishBatchRepository;
        this.publishTaskRepository = publishTaskRepository;
        this.contentRepository = contentRepository;
        this.platformContentRepository = platformContentRepository;
        this.authUserProvider = authUserProvider;
        this.idempotencyService = idempotencyService;
        this.publishOutboxService = publishOutboxService;
        this.publishOutboxProcessor = publishOutboxProcessor;
    }

    @Transactional
    public PublishBatchResponse create(PublishBatchCreateRequest request) {
        Long userId = authUserProvider.currentUserId();
        return findByRequestId(userId, request.requestId())
                .orElseGet(() -> createWithIdempotencyGuard(request, userId));
    }

    private PublishBatchResponse createWithIdempotencyGuard(PublishBatchCreateRequest request, Long userId) {
        if (!idempotencyService.acquirePublishRequest(userId, request.requestId())) {
            return findByRequestId(userId, request.requestId())
                    .orElseThrow(() -> new IllegalArgumentException("duplicate publish request is still processing"));
        }
        return findByRequestId(userId, request.requestId())
                .orElseGet(() -> createNewBatch(request, userId));
    }

    private java.util.Optional<PublishBatchResponse> findByRequestId(Long userId, String requestId) {
        return publishBatchRepository.findByUserIdAndRequestIdAndDeletedFalse(userId, requestId)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PublishBatchResponse get(Long batchId) {
        PublishBatch batch = findOwnedBatch(batchId);
        return toResponse(batch);
    }

    @Transactional(readOnly = true)
    public PublishTaskResponse getTask(Long taskId) {
        PublishTask task = publishTaskRepository
                .findByIdAndUserIdAndDeletedFalse(taskId, authUserProvider.currentUserId())
                .orElseThrow(() -> new EntityNotFoundException("publish task not found"));
        return toTaskResponse(task);
    }

    @Transactional
    public PublishTaskResponse retryTask(Long taskId) {
        PublishTask task = publishTaskRepository
                .findByIdAndUserIdAndDeletedFalse(taskId, authUserProvider.currentUserId())
                .orElseThrow(() -> new EntityNotFoundException("publish task not found"));
        if (task.getStatus() != PublishTaskStatus.FAILED) {
            throw new IllegalArgumentException("only failed tasks can be retried");
        }
        task.setStatus(PublishTaskStatus.RETRYING);
        task.setRetryCount(0);
        task.setErrorMessage(null);
        task.setResultUrl(null);
        task.setPublishedAt(null);
        publishTaskRepository.flush();
        publishOutboxService.enqueuePublishTask(task.getId());
        processOutboxAfterCommit();
        return toTaskResponse(task);
    }

    private PublishBatchResponse createNewBatch(PublishBatchCreateRequest request, Long userId) {
        ContentItem content = contentRepository.findByIdAndUserIdAndDeletedFalse(request.contentId(), userId)
                .orElseThrow(() -> new EntityNotFoundException("content not found"));
        List<Platform> platforms = request.platforms() == null || request.platforms().isEmpty()
                ? Arrays.asList(Platform.values())
                : request.platforms();
        List<PlatformContent> platformContents = platforms.stream()
                .map(platform -> findPlatformContent(content, platform, userId))
                .toList();

        PublishBatch batch = new PublishBatch();
        batch.setUserId(userId);
        batch.setContentId(content.getId());
        batch.setRequestId(request.requestId());
        batch.setStatus(PublishBatchStatus.PUBLISHING);
        batch.setTaskCount(platformContents.size());
        publishBatchRepository.save(batch);

        List<PublishTask> tasks = platformContents.stream().map(platformContent -> {
            PublishTask task = new PublishTask();
            task.setUserId(userId);
            task.setBatchId(batch.getId());
            task.setPlatformContentId(platformContent.getId());
            task.setPlatform(platformContent.getPlatform());
            task.setStatus(PublishTaskStatus.PENDING);
            return publishTaskRepository.save(task);
        }).toList();
        publishTaskRepository.flush();
        tasks.forEach(task -> publishOutboxService.enqueuePublishTask(task.getId()));
        processOutboxAfterCommit();
        return toResponse(batch);
    }

    private void processOutboxAfterCommit() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            publishOutboxProcessor.processDueEvents();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publishOutboxProcessor.processDueEvents();
            }
        });
    }

    private PlatformContent findPlatformContent(ContentItem content, Platform platform, Long userId) {
        return platformContentRepository
                .findByContentIdAndPlatformAndSourceVersionAndUserIdAndDeletedFalse(
                        content.getId(), platform, content.getVersion(), userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "platform content not generated for " + platform + " and content version " + content.getVersion()));
    }

    private PublishBatch findOwnedBatch(Long batchId) {
        return publishBatchRepository.findByIdAndUserIdAndDeletedFalse(batchId, authUserProvider.currentUserId())
                .orElseThrow(() -> new EntityNotFoundException("publish batch not found"));
    }

    private PublishBatchResponse toResponse(PublishBatch batch) {
        List<PublishTaskResponse> tasks = publishTaskRepository
                .findByBatchIdAndUserIdAndDeletedFalseOrderByPlatformAsc(batch.getId(), batch.getUserId())
                .stream()
                .map(this::toTaskResponse)
                .toList();
        return new PublishBatchResponse(
                batch.getId(),
                batch.getContentId(),
                batch.getRequestId(),
                batch.getStatus(),
                batch.getTaskCount(),
                tasks,
                batch.getCreatedAt(),
                batch.getUpdatedAt());
    }

    private PublishTaskResponse toTaskResponse(PublishTask task) {
        return new PublishTaskResponse(
                task.getId(),
                task.getBatchId(),
                task.getPlatformContentId(),
                task.getPlatform(),
                task.getStatus(),
                task.getRetryCount(),
                task.getResultUrl(),
                task.getErrorMessage(),
                task.getPublishedAt(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }
}
