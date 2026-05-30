package com.example.multipost.adapter;

import com.example.multipost.platform.Platform;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformContentRepository extends JpaRepository<PlatformContent, Long> {
    List<PlatformContent> findByContentIdAndUserIdAndDeletedFalseOrderByPlatformAsc(Long contentId, Long userId);

    Optional<PlatformContent> findByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    Optional<PlatformContent> findByContentIdAndPlatformAndSourceVersionAndUserIdAndDeletedFalse(
            Long contentId, Platform platform, int sourceVersion, Long userId);
}
