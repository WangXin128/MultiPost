package com.example.multipost.publish;

import com.example.multipost.adapter.PlatformContent;
import org.springframework.stereotype.Component;

@Component
public class MockPublisher {
    public String publish(PublishTask task, PlatformContent content) {
        return "/mock-posts/" + task.getPlatform().name().toLowerCase() + "/" + task.getId();
    }
}
