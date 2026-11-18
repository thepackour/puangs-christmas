package com.puang.global.worker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

class WorldWorkerTest {

    private WorldWorker worldWorker;

    @BeforeEach
    void setUp() {
        worldWorker = new WorldWorker();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        worldWorker.shutdown();
    }

    @Test
    void runsTaskOnWorldWorkerThreadAndReturnsResult() throws Exception {
        String threadName = worldWorker.submit(() -> Thread.currentThread().getName())
                .get(1, TimeUnit.SECONDS);

        assertThat(threadName).isEqualTo("world-worker");
    }

    @Test
    void tasksFromManyThreadsNeverRunAtTheSameTime() throws Exception {
        int requests = 500;
        AtomicInteger running = new AtomicInteger();
        AtomicInteger maxRunning = new AtomicInteger();
        int[] counter = {0}; // not thread-safe on purpose

        ExecutorService clients = Executors.newFixedThreadPool(16);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<CompletableFuture<Integer>>> submitted = new ArrayList<>();

        for (int i = 0; i < requests; i++) {
            submitted.add(clients.submit(() -> {
                start.await();
                return worldWorker.submit(() -> {
                    maxRunning.accumulateAndGet(running.incrementAndGet(), Math::max);
                    int value = ++counter[0];
                    running.decrementAndGet();
                    return value;
                });
            }));
        }
        start.countDown();

        for (Future<CompletableFuture<Integer>> f : submitted) {
            f.get(5, TimeUnit.SECONDS).get(5, TimeUnit.SECONDS);
        }
        clients.shutdown();

        assertThat(maxRunning.get()).isEqualTo(1);
        assertThat(counter[0]).isEqualTo(requests);
    }

    @Test
    void rejectsWhenQueueIsFull() throws Exception {
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch blocking = new CountDownLatch(1);

        // Occupy the single worker thread.
        worldWorker.submit(() -> {
            blocking.countDown();
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return null;
        });
        blocking.await(1, TimeUnit.SECONDS);

        // Fill the queue (capacity 1000).
        for (int i = 0; i < 1000; i++) {
            worldWorker.submit(() -> null);
        }

        assertThatThrownBy(() -> worldWorker.submit(() -> null))
                .isInstanceOf(RejectedExecutionException.class);

        release.countDown();
    }

    @Test
    void exceptionInTaskCompletesFutureExceptionally() {
        CompletableFuture<Object> future = worldWorker.submit(() -> {
            throw new IllegalStateException("no moves left");
        });

        assertThatThrownBy(() -> future.get(1, TimeUnit.SECONDS))
                .isInstanceOf(ExecutionException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }
}
