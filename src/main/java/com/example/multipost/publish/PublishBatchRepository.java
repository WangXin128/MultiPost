package com.example.multipost.publish;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublishBatchRepository extends JpaRepository<PublishBatch, Long> {
    Optional<PublishBatch> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    Optional<PublishBatch> findByUserIdAndRequestIdAndDeletedFalse(Long userId, String requestId);
}
