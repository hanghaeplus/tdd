package io.hhplus.tdd.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ConcurrentExecutorUtils {

    public static void execute(int threadCount, Consumer<Integer> consumer) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
            CountDownLatch latch = new CountDownLatch(threadCount);

            IntStream.range(0, threadCount).forEach(i -> {
                executorService.execute(() -> {
                    try {
                        consumer.accept(i);
                    } finally {
                        latch.countDown();
                    }
                });
            });

            latch.await();
            executorService.shutdown();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
