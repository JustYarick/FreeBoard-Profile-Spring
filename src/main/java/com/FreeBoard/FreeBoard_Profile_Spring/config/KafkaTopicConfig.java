package com.FreeBoard.FreeBoard_Profile_Spring.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic ProfileCreateTopic() {
        return TopicBuilder.name("profileCreated")
                .build();
    }

    @Bean
    public NewTopic ProfileFailTopic() {
        return TopicBuilder.name("profileFailed")
                .build();
    }
}
