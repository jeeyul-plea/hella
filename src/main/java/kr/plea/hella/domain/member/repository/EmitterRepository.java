package kr.plea.hella.domain.member.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class EmitterRepository {

    private Map<String, SseEmitter> emitterMap = new HashMap<>();

    public SseEmitter save(String username, SseEmitter sseEmitter) {
        emitterMap.put(getKey(username), sseEmitter);
        log.debug("Save emitter for : {}", username);
        return sseEmitter;
    }

    public Optional<SseEmitter> get(String username) {
        log.debug("Get emitter for : {}", username);
        return Optional.ofNullable(emitterMap.get(getKey(username)));
    }

    public void delete(String username) {
        emitterMap.remove(getKey(username));
        log.debug("Delete emitter for : {}", username);
    }

    private String getKey(String username) {
        return "Emitter:UID:" + username;
    }
}
