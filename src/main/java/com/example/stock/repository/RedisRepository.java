package com.example.stock.repository;

import java.time.Duration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisRepository {

	private RedisTemplate<String, String> redisTemplate;

	public RedisRepository(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public Boolean lock(Long key) {
		return redisTemplate
			.opsForValue()
			.setIfAbsent(key.toString(), "lock", Duration.ofMillis(3_000));
	}

	public Boolean unlock(Long key) {
		return redisTemplate.delete(key.toString());
	}

}
