package ca.bc.gov.open.sender.services;

import ca.bc.gov.open.icon.models.HealthServicePub;
import ca.bc.gov.open.icon.models.PingModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsumerService {
    private final HSRService hsrService;

    private final ObjectMapper objectMapper;

    @Autowired
    public ConsumerService(ObjectMapper objectMapper, HSRService hsrService) {
        this.objectMapper = objectMapper;
        this.hsrService = hsrService;
    }

    @RabbitListener(queues = "${icon.hsr-queue}")
    public void receiveHSRMessage(@Payload Message<HealthServicePub> message)
            throws IOException, InterruptedException {
        try {
            hsrService.processHSR(message.getPayload());
        } catch (Exception ignored) {
            log.error("ERROR: " + message + " not processed successfully");
        }
        System.out.println(objectMapper.writeValueAsString(message.getPayload()));
    }

    @RabbitListener(queues = "${icon.ping-queue}")
    public void receivePingMessage(@Payload Message<PingModel> message)
            throws JsonProcessingException {
        System.out.println(new ObjectMapper().writeValueAsString(message.getPayload()));
    }
}
