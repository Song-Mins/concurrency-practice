package com.example.stock.facade;

import com.example.stock.service.StockService;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
public class StockFacade {

	private RedissonClient redissonClient;

	private StockService stockService;

	public StockFacade(RedissonClient redissonClient, StockService stockService) {
		this.redissonClient = redissonClient;
		this.stockService = stockService;
	}

	public void decrease(Long key, Long quantity) {
		RLock lock = redissonClient.getLock(key.toString());

		try {
			boolean available = lock.tryLock(15, 1, TimeUnit.SECONDS);

			if (!available) {
				System.out.println("lock 획득 실패");
				return;
			}

			stockService.decrease(key, quantity);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

}
