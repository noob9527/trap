package cn.staynoob.trap.java.basic.concurrent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

public class ExecutorSpec {
    @Test
    @DisplayName("use ThreadFactory to create daemon threads")
    void test100() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });

        AtomicBoolean isDaemon = new AtomicBoolean(false);

        executor.submit(() -> {
            isDaemon.set(Thread.currentThread().isDaemon());
        });

        executor.awaitTermination(1, TimeUnit.SECONDS);

        assertThat(isDaemon).isTrue();
    }

    @Test
    @DisplayName("interrupt a thread won't effect subsequent task")
    void test200() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        AtomicReference<Thread> thread = new AtomicReference<>();
        Callable<Boolean> callable = () -> {
            try {
                thread.set(Thread.currentThread());
                Thread.sleep(200);
                return true;
            } catch (InterruptedException e) {
                return false;
            }
        };
        Future<Boolean> future1 = executor.submit(callable);
        Thread.sleep(100);
        future1.cancel(true);
        Future<Boolean> future2 = executor.submit(callable);
        assertThat(future2.get()).isTrue();
        assertThat(thread.get().getState()).isEqualTo(Thread.State.WAITING);
    }

    @Nested
    class AwaitTerminationSpec {
        @Test
        @DisplayName("awaitTermination should return true")
        void test100() throws InterruptedException {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Runnable runnable = () -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };

            Future<?> future = executorService.submit(runnable);

            executorService.shutdown();
            boolean terminated = executorService
                    .awaitTermination(200, TimeUnit.MILLISECONDS);

            assertThat(terminated).isTrue();
            assertThat(future).isDone();
        }

        @Test
        @DisplayName("awaitTermination should return false")
        void test200() throws InterruptedException {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Runnable runnable = () -> {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };

            executorService.submit(runnable);

            executorService.shutdown();
            boolean terminated = executorService
                    .awaitTermination(100, TimeUnit.MILLISECONDS);

            assertThat(terminated).isFalse();
        }
    }

}
