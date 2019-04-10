package cn.staynoob.trap.java.guava.concurrent;

import com.google.common.util.concurrent.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

public class ListenableFutureSpec {

    @Nested
    @DisplayName("note that add onFailure handler won't catch the exception")
    class ExceptionHandling {
        @Test
        void test100() {
            ListeningExecutorService service = MoreExecutors
                    .listeningDecorator(Executors.newSingleThreadExecutor());

            RuntimeException e = new RuntimeException("gotcha");

            ListenableFuture<Boolean> future = service.submit(() -> {
                throw e;
            });

            FutureCallback<Boolean> callback = new FutureCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    throw new RuntimeException("this line won't be executed");
                }

                @Override
                public void onFailure(Throwable t) {
                    assertThat(t).isEqualTo(e);
                }
            };

            Futures.addCallback(future, callback, MoreExecutors.directExecutor());

            assertThatThrownBy(future::get)
                    .hasCause(e);
        }
    }

    @Nested
    class Cancellation {
        @Test
        @DisplayName("invoke get in a cancelled Future instance should throw CancellationException")
        void test100() {
            ListeningExecutorService service = MoreExecutors
                    .listeningDecorator(Executors.newSingleThreadExecutor());

            ListenableFuture<?> future = service.submit(() -> {
            });

            boolean cancelled = future.cancel(false);
            assertThat(cancelled).isTrue();
            assertThatThrownBy(future::get)
                    .isInstanceOf(CancellationException.class);
        }

        @Test
        @DisplayName("cancel a finished completableFuture should have no effect")
        void test200() throws ExecutionException, InterruptedException {
            ListeningExecutorService service = MoreExecutors
                    .listeningDecorator(Executors.newSingleThreadExecutor());

            ListenableFuture<Boolean> future = service.submit(() -> true);
            future.get();

            boolean cancelled = future.cancel(false);

            assertThat(cancelled).isFalse();
            assertThat(future.get()).isTrue();
        }

        @SuppressWarnings("Duplicates")
        @Test
        @DisplayName("cancelling a running computation with parameter true will interrupt the computation")
        void test300() throws InterruptedException {
            AtomicBoolean interrupted = new AtomicBoolean(false);
            AtomicInteger counter = new AtomicInteger(0);
            ListeningExecutorService service = MoreExecutors
                    .listeningDecorator(Executors.newSingleThreadExecutor());
            ListenableFuture<?> future = service.submit(() -> {
                try {
                    while (true) {
                        counter.incrementAndGet();
                        Thread.sleep(100);
                    }
                } catch (InterruptedException e) {
                    interrupted.set(true);
                }
            });

            Thread.sleep(100); // give the working thread an opportunity to start

            Boolean cancelled = future.cancel(true);

            // prove that cancellation is successful
            assertThat(cancelled).isTrue();
            assertThat(future.isCancelled()).isTrue();
            assertThat(future.isDone()).isTrue();

            Thread.sleep(1000);

            // If the cancellation terminates the working thread
            // the counter should have value around 1
            assertThat(counter).hasValueBetween(1, 4);
            assertThat(interrupted).isTrue();

            assertThatThrownBy(future::get)
                    .isInstanceOf(CancellationException.class);
        }

        @Test
        @DisplayName("The underlying thread can be reused once a future is cancelled")
        void test400() throws InterruptedException {
            ListeningExecutorService service = MoreExecutors
                    .listeningDecorator(Executors.newSingleThreadExecutor());

            AtomicInteger counter1 = new AtomicInteger(0);
            AtomicInteger counter2 = new AtomicInteger(0);


            class CounterRunnable implements Runnable {
                private AtomicInteger counter;

                public CounterRunnable(AtomicInteger counter) {
                    this.counter = counter;
                }

                @Override
                public void run() {
                    try {
                        while (true) {
                            counter.incrementAndGet();
                            Thread.sleep(100);
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }

            Runnable runnable1 = new CounterRunnable(counter1);
            Runnable runnable2 = new CounterRunnable(counter2);

            ListenableFuture<?> future1 = service.submit(runnable1);

            Thread.sleep(100L); // let future1 run first

            ListenableFuture<?> future2 = service.submit(runnable2);

            future1.cancel(true);

            Thread.sleep(100L);

            future2.cancel(true);

            assertThat(counter1).hasValueBetween(1, 3);
            assertThat(counter1).hasValueBetween(1, 3);
        }

        @Test
        @DisplayName("cancel a future should call FutureCallback.onFailure with CancellationException")
        void test500() {
            AtomicBoolean executed = new AtomicBoolean(false);
            ListeningExecutorService service = MoreExecutors
                    .listeningDecorator(Executors.newSingleThreadExecutor());

            ListenableFuture<?> future = service.submit(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ignored) {
                }
            });

            FutureCallback<Object> callback = new FutureCallback<Object>() {
                @Override
                public void onSuccess(Object result) {
                    fail("should not be executed");
                }

                @Override
                public void onFailure(Throwable t) {
                    executed.set(true);
                    assertThat(t).isInstanceOf(CancellationException.class);
                }
            };

            Futures.addCallback(future, callback, MoreExecutors.directExecutor());
            boolean cancelled = future.cancel(false);

            assertThat(cancelled).isTrue();
            assertThat(executed).isTrue();
        }
    }
}
