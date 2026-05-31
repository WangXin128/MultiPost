package com.example.multipost.platform;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformCapabilityRepository extends JpaRepository<PlatformCapability, Long> {
    Optional<PlatformCapability> findByPlatformAndDeletedFalse(Platform platform);

    List<PlatformCapability> findByEnabledTrueAndDeletedFalseOrderByPlatformAsc();

    boolean existsByPlatformAndDeletedFalse(Platform platform);
}
