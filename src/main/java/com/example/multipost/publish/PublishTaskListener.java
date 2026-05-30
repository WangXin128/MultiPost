package com.example.multipost.publish;

import com.example.multipost.config.RabbitPublishConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "multipost.rabbit", name = "enabled", havingValue = "true")
public class PublishTaskListener {
    private final PublishExecutionService publishExecutionService;

    public PublishTaskListener(PublishExecutionService publishExecutionService) {
        this.publishExecutionService = publishExecutionService;
    }

    @RabbitListener(queues = RabbitPublishConfig.QUEUE)
    public void handle(PublishTaskMessage message) {
        publishExecutionService.execute(message.taskId());
    }
}
