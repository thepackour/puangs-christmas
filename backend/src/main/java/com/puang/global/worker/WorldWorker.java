package com.puang.global.worker;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.function.Supplier;

@Component
public class WorldWorker {

    private final ExecutorService worker = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000),
            r -> new Thread(r, "world-worker"),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public <T> CompletableFuture<T> submit(
            Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, worker);
    }

    @PreDestroy
    void shutdown() throws InterruptedException {
        worker.shutdown();
        if (!worker.awaitTermination(5, TimeUnit.SECONDS)) {
            worker.shutdownNow();
        }
    }

}
