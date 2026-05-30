package com.example.multipost.publish;

import com.example.multipost.adapter.PlatformContent;
import org.springframework.stereotype.Component;

@Component
public class MockPublisher {
    public String publish(PublishTask task, PlatformContent content) {
        if (content.getBody() != null && content.getBody().contains("[mock-fail]")) {
            throw new IllegalStateException("mock publish failure requested by content body");
        }
        if (content.getBody() != null && content.getBody().contains("[mock-fail-once]") && task.getRetryCount() == 0) {
            throw new IllegalStateException("mock publish transient failure requested by content body");
        }
        return "/mock-posts/" + task.getPlatform().name().toLowerCase() + "/" + task.getId();
    }
}
