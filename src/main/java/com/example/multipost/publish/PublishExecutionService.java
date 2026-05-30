package com.example.multipost.publish;

import com.example.multipost.adapter.PlatformContent;
import com.example.multipost.adapter.PlatformContentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishExecutionService {
    private final PublishTaskRepository publishTaskRepository;
    private final PublishBatchRepository publishBatchRepository;
    private final PublishLogRepository publishLogRepository;
    private final PlatformContentRepository platformContentRepository;
    private final MockPublisher mockPublisher;

    public PublishExecutionService(
            PublishTaskRepository publishTaskRepository,
            PublishBatchRepository publishBatchRepository,
            PublishLogRepository publishLogRepository,
            PlatformContentRepository platformContentRepository,
            MockPublisher mockPublisher) {
        this.publishTaskRepository = publishTaskRepository;
        this.publishBatchRepository = publishBatchRepository;
        this.publishLogRepository = publishLogRepository;
        this.platformContentRepository = platformContentRepository;
        this.mockPublisher = mockPublisher;
    }

    @Transactional
    public void execute(Long taskId) {
        int claimed = publishTaskRepository.claimForPublishing(
                taskId,
                PublishTaskStatus.PUBLISHING,
                List.of(PublishTaskStatus.PENDING, PublishTaskStatus.RETRYING));
        if (claimed == 0) {
            return;
        }

        PublishTask task = publishTaskRepository.findByIdAndDeletedFalse(taskId)
                .orElseThrow(() -> new EntityNotFoundException("publish task not found"));
        log(task.getId(), PublishTaskStatus.PENDING, PublishTaskStatus.PUBLISHING, "task claimed for publishing");

        try {
            PlatformContent platformContent = platformContentRepository.findById(task.getPlatformContentId())
                    .filter(content -> !content.isDeleted())
                    .orElseThrow(() -> new EntityNotFoundException("platform content not found"));
            String resultUrl = mockPublisher.publish(task, platformContent);
            transitionToSuccess(task, resultUrl);
        } catch (Exception ex) {
            transitionToFailed(task, ex.getMessage());
        }
        refreshBatchStatus(task.getBatchId());
    }

    private void transitionToSuccess(PublishTask task, String resultUrl) {
        task.setStatus(PublishTaskStatus.SUCCESS);
        task.setResultUrl(resultUrl);
        task.setErrorMessage(null);
        task.setPublishedAt(Instant.now());
        log(task.getId(), PublishTaskStatus.PUBLISHING, PublishTaskStatus.SUCCESS, "mock publish success");
    }

    private void transitionToFailed(PublishTask task, String message) {
        task.setStatus(PublishTaskStatus.FAILED);
        task.setErrorMessage(message == null ? "mock publish failed" : message);
        log(task.getId(), PublishTaskStatus.PUBLISHING, PublishTaskStatus.FAILED, task.getErrorMessage());
    }

    private void refreshBatchStatus(Long batchId) {
        PublishBatch batch = publishBatchRepository.findById(batchId)
                .orElseThrow(() -> new EntityNotFoundException("publish batch not found"));
        List<PublishTask> tasks = publishTaskRepository.findByBatchIdAndDeletedFalse(batchId);
        long successCount = tasks.stream().filter(task -> task.getStatus() == PublishTaskStatus.SUCCESS).count();
        long failedCount = tasks.stream().filter(task -> task.getStatus() == PublishTaskStatus.FAILED).count();

        if (successCount == tasks.size()) {
            batch.setStatus(PublishBatchStatus.ALL_SUCCESS);
        } else if (failedCount == tasks.size()) {
            batch.setStatus(PublishBatchStatus.ALL_FAILED);
        } else if (successCount + failedCount == tasks.size()) {
            batch.setStatus(PublishBatchStatus.PARTIAL_SUCCESS);
        } else {
            batch.setStatus(PublishBatchStatus.PUBLISHING);
        }
    }

    private void log(Long taskId, PublishTaskStatus fromStatus, PublishTaskStatus toStatus, String message) {
        PublishLog log = new PublishLog();
        log.setTaskId(taskId);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setMessage(message);
        publishLogRepository.save(log);
    }
}
