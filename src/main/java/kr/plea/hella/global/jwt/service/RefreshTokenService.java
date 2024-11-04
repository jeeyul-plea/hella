package kr.plea.hella.global.jwt.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    public void setRefreshToken(String key, String refreshToken) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, refreshToken);
    }

    public void deleteRefreshToken(String key) {
        redisTemplate.delete(key);
    }

    public String getRefreshToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
