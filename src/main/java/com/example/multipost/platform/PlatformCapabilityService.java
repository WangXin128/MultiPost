package com.example.multipost.platform;

import com.example.multipost.platform.dto.PlatformCapabilityResponse;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformCapabilityService {
    private final PlatformCapabilityRepository platformCapabilityRepository;

    public PlatformCapabilityService(PlatformCapabilityRepository platformCapabilityRepository) {
        this.platformCapabilityRepository = platformCapabilityRepository;
    }

    @PostConstruct
    @Transactional
    public void initializeDefaults() {
        createIfMissing(Platform.WECHAT, "WeChat Official Account", PublishMode.API, AuthType.APP_SECRET,
                true, true, true, "Official account API can publish drafts after account verification.");
        createIfMissing(Platform.ZHIHU, "Zhihu", PublishMode.MANUAL, AuthType.USER_CONFIRMATION,
                false, false, false, "Public write API is not assumed; use copy/export and manual confirmation.");
        createIfMissing(Platform.BILIBILI, "Bilibili", PublishMode.API, AuthType.OAUTH2,
                true, true, true, "Open platform publishing requires application review and user authorization.");
        createIfMissing(Platform.XIAOHONGSHU, "Xiaohongshu", PublishMode.CLIENT_ASSISTED, AuthType.USER_CONFIRMATION,
                false, false, true, "Prefer assisted/manual publishing instead of unofficial server automation.");
    }

    @Transactional(readOnly = true)
    public List<PlatformCapabilityResponse> listEnabled() {
        return platformCapabilityRepository.findByEnabledTrueAndDeletedFalseOrderByPlatformAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void createIfMissing(
            Platform platform,
            String displayName,
            PublishMode publishMode,
            AuthType authType,
            boolean supportsSchedule,
            boolean supportsStatusQuery,
            boolean supportsMediaUpload,
            String notes) {
        if (platformCapabilityRepository.existsByPlatformAndDeletedFalse(platform)) {
            return;
        }
        PlatformCapability capability = new PlatformCapability();
        capability.setPlatform(platform);
        capability.setDisplayName(displayName);
        capability.setPublishMode(publishMode);
        capability.setAuthType(authType);
        capability.setSupportsSchedule(supportsSchedule);
        capability.setSupportsStatusQuery(supportsStatusQuery);
        capability.setSupportsMediaUpload(supportsMediaUpload);
        capability.setEnabled(true);
        capability.setNotes(notes);
        platformCapabilityRepository.save(capability);
    }

    private PlatformCapabilityResponse toResponse(PlatformCapability capability) {
        return new PlatformCapabilityResponse(
                capability.getPlatform(),
                capability.getDisplayName(),
                capability.getPublishMode(),
                capability.getAuthType(),
                capability.isSupportsSchedule(),
                capability.isSupportsStatusQuery(),
                capability.isSupportsMediaUpload(),
                capability.isEnabled(),
                capability.getNotes());
    }
}
