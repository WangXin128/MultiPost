package com.example.multipost.publish;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PublishTaskRepository extends JpaRepository<PublishTask, Long> {
    List<PublishTask> findByBatchIdAndUserIdAndDeletedFalseOrderByPlatformAsc(Long batchId, Long userId);

    List<PublishTask> findByBatchIdAndDeletedFalse(Long batchId);

    Optional<PublishTask> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    Optional<PublishTask> findByIdAndDeletedFalse(Long id);

    @Modifying
    @Query("""
            update PublishTask t
            set t.status = :publishing
            where t.id = :taskId
              and t.deleted = false
              and t.status in :claimableStatuses
            """)
    int claimForPublishing(
            @Param("taskId") Long taskId,
            @Param("publishing") PublishTaskStatus publishing,
            @Param("claimableStatuses") List<PublishTaskStatus> claimableStatuses);
}
