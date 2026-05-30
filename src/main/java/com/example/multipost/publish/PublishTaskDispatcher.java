package com.example.multipost.publish;

import com.example.multipost.config.RabbitPublishConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PublishTaskDispatcher {
    private final RabbitTemplate rabbitTemplate;
    private final PublishExecutionService publishExecutionService;
    private final boolean rabbitEnabled;

    public PublishTaskDispatcher(
            RabbitTemplate rabbitTemplate,
            PublishExecutionService publishExecutionService,
            @Value("${multipost.rabbit.enabled:false}") boolean rabbitEnabled) {
        this.rabbitTemplate = rabbitTemplate;
        this.publishExecutionService = publishExecutionService;
        this.rabbitEnabled = rabbitEnabled;
    }

    public void dispatch(Long taskId) {
        if (rabbitEnabled) {
            rabbitTemplate.convertAndSend(
                    RabbitPublishConfig.EXCHANGE,
                    RabbitPublishConfig.ROUTING_KEY,
                    new PublishTaskMessage(taskId));
            return;
        }
        publishExecutionService.execute(taskId);
    }
}
