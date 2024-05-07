package catchytape.spring.auth.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.cache.redis.time-to-live}")
    private int expireSeconds;

    public void setValue(String key, String value) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();

        values.set(key, value, Duration.ofSeconds(expireSeconds));
        log.info("만료 시간 : ", Duration.ofSeconds(expireSeconds));
    }

    @Transactional(readOnly = true)
    public String getValue(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();

        return values.get(key);
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
}
