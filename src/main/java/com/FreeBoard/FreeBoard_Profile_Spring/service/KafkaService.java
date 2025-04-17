package com.FreeBoard.FreeBoard_Profile_Spring.service;

import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.ProfileCreatedEventDTO;
import com.FreeBoard.FreeBoard_Profile_Spring.model.DTO.ProfileFailedEventDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class KafkaService {

    private KafkaTemplate<String, ProfileCreatedEventDTO> profileCreatedEventKafkaTemplate;
    private KafkaTemplate<String, ProfileFailedEventDTO> profileFailedEventKafkaTemplate;

    public CompletableFuture<SendResult<String, ProfileCreatedEventDTO>> sendProfileCreatedEvent(ProfileCreatedEventDTO dto) {
        CompletableFuture<SendResult<String, ProfileCreatedEventDTO>> future =
                profileCreatedEventKafkaTemplate.send("profileCreated", dto);

        future.thenAccept(result -> logSendResult(result, "ProfileCreatedEvent"))
                .exceptionally(ex -> {
                    log.error("Failed to send ProfileCreatedEvent", ex);
                    return null;
                });

        return future;
    }

    public CompletableFuture<SendResult<String, ProfileFailedEventDTO>> sendProfileFailedEvent(ProfileFailedEventDTO dto) {
        CompletableFuture<SendResult<String, ProfileFailedEventDTO>> future =
                profileFailedEventKafkaTemplate.send("profileFailed", dto);

        future.thenAccept(result -> logSendResult(result, "ProfileFailedEvent"))
                .exceptionally(ex -> {
                    log.error("Failed to send ProfileFailedEvent", ex);
                    return null;
                });

        return future;
    }


    private <T> void logSendResult(SendResult<String, T> result, String eventType) {
        log.info("{} sent: topic={}, partition={}, offset={}",
                eventType,
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
    }
}
