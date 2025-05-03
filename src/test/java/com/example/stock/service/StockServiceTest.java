package com.example.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void insert() {
        Stock stock = new Stock(1L, 100L);
        stockRepository.save(stock);
    }

    @AfterEach
    public void delete() {
        stockRepository.deleteAll();
    }

    @Test
    public void 동시에_100명이_주문() throws InterruptedException {
        int threadCount = 100;

        // 크기가 32인 고정된 스레드 풀을 생성 (최대 32개 스레드가 동시에 실행됨)
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // CountDownLatch 초기화
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            // 메인 스레드가 스레드 풀에 작업을 제출함 (총 100개의 작업, 스레드가 부족하면 작업 큐에 대기)
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    // 작업이 끝났음을 메인스레드에 알림
                    latch.countDown();
                }
            });
        }

        //  메인 스레드가 100개의 작업 완료 대기
        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(0, stock.getQuantity());
    }
}