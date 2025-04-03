package io.hhplus.tdd.point.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointLockManagerTest {

    private UserPointLockManager lockManager;

    @BeforeEach
    void setUp() {
        lockManager = new UserPointLockManager();
    }

    @Test
    void test() throws InterruptedException {
        // given
        long userId = new Random().nextLong(1L, Long.MAX_VALUE);
        ThreadFactory factory = Executors.defaultThreadFactory();

        // when
        factory.newThread(() -> {
                    Lock lock = lockManager.getLock(userId);
                    lock.lock();

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ignored) {
                    } finally {
                        lock.unlock();
                    }
                })
                .start();
        Thread.sleep(100);
        boolean acquired = CompletableFuture.supplyAsync(() -> lockManager.getLock(userId).tryLock()).join();

        // then
        assertThat(acquired)
                .isFalse();

        // when
        Thread.sleep(300);
        acquired = CompletableFuture.supplyAsync(() -> lockManager.getLock(userId).tryLock()).join();

        // then
        assertThat(acquired)
                .isTrue();
    }

}
