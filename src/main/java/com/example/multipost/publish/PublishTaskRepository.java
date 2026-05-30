package com.example.multipost.publish;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublishTaskRepository extends JpaRepository<PublishTask, Long> {
    List<PublishTask> findByBatchIdAndUserIdAndDeletedFalseOrderByPlatformAsc(Long batchId, Long userId);

    Optional<PublishTask> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);
}
