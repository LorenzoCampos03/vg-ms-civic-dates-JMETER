package pe.edu.vallegrande.sigei.civicDates.infrastructure.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("jmeter")
public class JMeterDummyConfig {

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate() {
            @Override
            public void afterPropertiesSet() {
                // Do nothing to bypass ConnectionFactory validation on startup
            }
            
            @Override
            public void convertAndSend(String exchange, String routingKey, Object message) {
                // Dummy implementation to avoid connecting to RabbitMQ during JMeter tests
            }
        };
    }
}
