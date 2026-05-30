package com.example.multipost.content;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<ContentItem, Long> {
    Page<ContentItem> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    Optional<ContentItem> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);
}
