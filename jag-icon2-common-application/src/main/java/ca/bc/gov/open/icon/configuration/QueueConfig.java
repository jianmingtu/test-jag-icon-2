package ca.bc.gov.open.icon.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class QueueConfig {

    @Value("${icon.exchange-name}")
    private String topicExchangeName;

    @Value("${icon.hsr-queue}")
    private String hsrQueueName;

    @Value("${icon.ping-queue}")
    private String pingQueueName;

    @Value("${icon.hsr-routing-key}")
    private String hsrRoutingkey;

    @Value("${icon.ping-routing-key}")
    private String pingRoutingKey;
}
