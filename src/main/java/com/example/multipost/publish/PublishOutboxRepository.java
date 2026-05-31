package com.example.multipost.publish;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PublishOutboxRepository extends JpaRepository<PublishOutbox, Long> {
    @Query("""
            select o from PublishOutbox o
            where o.deleted = false
              and o.status = :status
              and (o.nextRetryAt is null or o.nextRetryAt <= :now)
            order by o.createdAt asc
            """)
    List<PublishOutbox> findDue(
            @Param("status") PublishOutboxStatus status,
            @Param("now") Instant now);
}
