package kr.plea.hella.domain.member.component;

import java.io.IOException;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.plea.hella.domain.member.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNotificationListener {
    private final EmitterRepository emitterRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void consumeNotification(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String username = jsonNode.get("username").asText();
            String notificationMessage = jsonNode.get("message").asText();
            Long notificationId = jsonNode.get("notificationId").asLong();

            emitterRepository.get(username).ifPresentOrElse(sseEmitter -> {
                try {
                    sseEmitter.send(
                        SseEmitter.event()
                            .id(notificationId.toString())
                            .name("notify")
                            .data(notificationMessage)
                    );
                } catch (IOException e) {
                    emitterRepository.delete(username);
                    log.error("Failed to send SSE notification", e);
                }
            }, () -> log.info("No emitter found for user {}", username));

        } catch (Exception e) {
            log.error("Failed to process Kafka message", e);
        }
    }
}

