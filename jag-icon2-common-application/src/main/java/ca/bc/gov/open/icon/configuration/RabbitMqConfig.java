package ca.bc.gov.open.icon.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    private final QueueConfig queueConfig;

    @Autowired
    public RabbitMqConfig(QueueConfig queueConfig) {
        this.queueConfig = queueConfig;
    }

    @Bean(name = "hsr-queue")
    public Queue hsrQueue() {
        return new Queue(queueConfig.getHsrQueueName(), false);
    }

    @Bean(name = "ping-queue")
    public Queue testQueue() {
        return new Queue(queueConfig.getPingQueueName(), false);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(queueConfig.getTopicExchangeName());
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Declarables binding(
            @Qualifier("hsr-queue") Queue hsrQueue,
            @Qualifier("ping-queue") Queue testQueue,
            DirectExchange exchange) {
        return new Declarables(
                BindingBuilder.bind(hsrQueue).to(exchange).with(queueConfig.getHsrRoutingkey()),
                BindingBuilder.bind(testQueue).to(exchange).with(queueConfig.getPingRoutingKey()));
    }

    @Bean
    public Jackson2JsonMessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
