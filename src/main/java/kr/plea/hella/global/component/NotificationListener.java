package kr.plea.hella.global.component;

import java.io.IOException;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
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
public class NotificationListener implements MessageListener {
    private final EmitterRepository emitterRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String jsonString = new String(message.getBody());

            // 이중 인코딩된 JSON을 해제
            JsonNode outerNode = objectMapper.readTree(jsonString);  // 첫 번째 파싱
            String innerJsonString = outerNode.asText(); // 안쪽의 실제 JSON 문자열 추출

            // 다시 JSON 파싱
            JsonNode jsonNode = objectMapper.readTree(innerJsonString);  // 두 번째 파싱

            // 필드 추출 확인
            JsonNode usernameNode = jsonNode.get("username");
            JsonNode messageNode = jsonNode.get("message");
            JsonNode notificationIdNode = jsonNode.get("notificationId");

            if (usernameNode == null || messageNode == null || notificationIdNode == null) {
                log.error("Invalid message format: missing fields in Redis message: {}", jsonString);
                return;
            }

            String username = usernameNode.asText();
            String notificationMessage = messageNode.asText();
            Long notificationId = notificationIdNode.asLong();

            log.debug("Parsed values - Username: {}, Message: {}, Notification ID: {}", username, notificationMessage, notificationId);

            // SSE를 통해 알림 전송
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
            log.error("Failed to process Redis message", e);
        }
    }

}
