package com.FreeBoard.FreeBoard_Profile_Spring.config;

import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.ProfileCreatedEventDTO;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.ProfileFailedEventDTO;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> commonProducerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return props;
    }

    @Bean
    public Map<String, Object> newUserEventProducerConfig() {
        Map<String, Object> props = commonProducerConfig();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    // profile created
    @Bean
    public ProducerFactory<String, ProfileCreatedEventDTO> profileCreatedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(newUserEventProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, ProfileCreatedEventDTO> profileCreatedKafkaTemplate() {
        return new KafkaTemplate<>(profileCreatedProducerFactory());
    }

    // profile creat fail
    @Bean
    public ProducerFactory<String, ProfileFailedEventDTO> profileFailedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(newUserEventProducerConfig());
    }

    @Bean
    public KafkaTemplate<String, ProfileFailedEventDTO> profileFailedKafkaTemplate() {
        return new KafkaTemplate<>(profileFailedProducerFactory());
    }
}
