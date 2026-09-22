package com.sprawler.spring.messaging.kafka.controller;

import com.sprawler.spring.messaging.kafka.dto.KafkaMessageDto;
import com.sprawler.spring.messaging.kafka.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaMessageController {

    private final KafkaProducerService producerService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody KafkaMessageDto message) {
        try {
            if (message.getId() == null) {
                message.setId(UUID.randomUUID().toString());
            }
            if (message.getTimestamp() == null) {
                message.setTimestamp(LocalDateTime.now());
            }
            
            producerService.sendMessageToDefaultTopic(message);
            return ResponseEntity.ok("Message sent successfully with ID: " + message.getId());
        } catch (Exception e) {
            log.error("Failed to send message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send message: " + e.getMessage());
        }
    }

    @PostMapping("/send/{topic}")
    public ResponseEntity<String> sendMessageToTopic(
            @PathVariable String topic,
            @RequestBody KafkaMessageDto message) {
        try {
            if (message.getId() == null) {
                message.setId(UUID.randomUUID().toString());
            }
            if (message.getTimestamp() == null) {
                message.setTimestamp(LocalDateTime.now());
            }
            
            producerService.sendMessage(topic, message);
            return ResponseEntity.ok("Message sent to topic '" + topic + "' with ID: " + message.getId());
        } catch (Exception e) {
            log.error("Failed to send message to topic: {}", topic, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to send message: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Kafka API is up and running");
    }
}
