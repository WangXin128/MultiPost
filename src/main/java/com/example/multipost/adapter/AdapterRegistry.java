package com.example.multipost.adapter;

import com.example.multipost.platform.Platform;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AdapterRegistry {
    private final Map<Platform, PlatformAdapter> adapters = new EnumMap<>(Platform.class);

    public AdapterRegistry(List<PlatformAdapter> adapterList) {
        for (PlatformAdapter adapter : adapterList) {
            adapters.put(adapter.getPlatform(), adapter);
        }
    }

    public PlatformAdapter get(Platform platform) {
        PlatformAdapter adapter = adapters.get(platform);
        if (adapter == null) {
            throw new IllegalArgumentException("unsupported platform: " + platform);
        }
        return adapter;
    }

    public List<Platform> supportedPlatforms() {
        return List.copyOf(adapters.keySet());
    }
}
