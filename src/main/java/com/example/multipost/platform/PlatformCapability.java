package com.example.multipost.platform;

import com.example.multipost.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "platform_capabilities", indexes = {
        @Index(name = "idx_platform_capabilities_platform", columnList = "platform", unique = true)
})
public class PlatformCapability extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, unique = true)
    private Platform platform;

    @Column(nullable = false, length = 80)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PublishMode publishMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AuthType authType;

    @Column(nullable = false)
    private boolean supportsSchedule;

    @Column(nullable = false)
    private boolean supportsStatusQuery;

    @Column(nullable = false)
    private boolean supportsMediaUpload;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false, length = 240)
    private String notes;
}
