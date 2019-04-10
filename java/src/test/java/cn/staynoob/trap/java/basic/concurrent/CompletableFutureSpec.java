package cn.staynoob.trap.java.basic.concurrent;

import cn.staynoob.trap.java.basic.utils.TestThread;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * reference: https://www.baeldung.com/java-completablefuture
 */
public class CompletableFutureSpec {
    @Test
    @DisplayName("cancel computation should throw CancellationException")
    void test100() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.cancel(false); // the parameter has no effect in the implementation
        assertThatThrownBy(future::get)
                .isInstanceOf(CancellationException.class);
    }

    @Nested
    class Creation {
        @Test
        @DisplayName("create instance with no-arg constructor")
        void test100() throws ExecutionException, InterruptedException {
            CompletableFuture<Boolean> future = new CompletableFuture<>();

            TestThread thread = new TestThread(() -> {
                try {
                    Thread.sleep(100);
                    future.complete(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            thread.start();

            boolean res = future.get();
            assertThat(thread.getState()).isEqualTo(Thread.State.TERMINATED);
            assertThat(res).isTrue();
        }

        /**
         * 类似于 js 中的 Promise.resolve();
         *
         * @throws ExecutionException
         * @throws InterruptedException
         */
        @Test
        @DisplayName("create instance with completed Future static method")
        void test200() throws ExecutionException, InterruptedException {
            CompletableFuture<Boolean> future = CompletableFuture.completedFuture(true);
            assertThat(future.get()).isTrue();
        }

        @Test
        @DisplayName("create instance with runAsync static method")
        void test300() throws ExecutionException, InterruptedException {
            CompletableFuture future = CompletableFuture.runAsync(() -> {
            });
            assertThat(future.get()).isNull();
        }

        @Test
        @DisplayName("create instance with applyAsync static method")
        void test400() throws ExecutionException, InterruptedException {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> true);
            assertThat(future.get()).isTrue();
        }
    }

    // 类似于 js 中的 Promise.prototype.then 方法
    @Nested
    class Chaining {

        @Test
        @DisplayName("then run")
        void test50() {
            Runnable runnable = Mockito.spy(Runnable.class);

            CompletableFuture<Object> future = new CompletableFuture<>();

            future.thenRun(runnable);

            Mockito.verify(runnable, Mockito.never()).run();
            future.complete(null);
            Mockito.verify(runnable, Mockito.times(1)).run();
        }

        @Test
        @DisplayName("then accept")
        void test100() {
            @SuppressWarnings("unchecked")
            Consumer<String> consumer = (Consumer<String>) Mockito.spy(Consumer.class);

            CompletableFuture<String> future = new CompletableFuture<>();

            future.thenAccept(consumer);

            Mockito.verify(consumer, Mockito.never()).accept(Mockito.anyString());
            future.complete("hello world");
            Mockito.verify(consumer).accept("hello world");
        }

        /**
         * 类似于 js 中的
         * promise.then((res) => {
         * return anotherFunction(res);
         * });
         */
        @Test
        @DisplayName("then apply")
        void test200() throws ExecutionException, InterruptedException {
            CompletableFuture<String> future = CompletableFuture.completedFuture("hello");
            future = future.thenApply(s -> s + " world");
            assertThat(future.get()).isEqualTo("hello world");
        }

        /**
         * 类似于 js 中的
         * promise.then((res) => {
         * return anotherAsyncFunction(res);
         * });
         * 这里的 anotherAsyncFunction 返回 Promise 实例
         */
        @Test
        @DisplayName("then compose")
        void test300() throws ExecutionException, InterruptedException {
            CompletableFuture<String> future = CompletableFuture.completedFuture("hello");
            future = future.thenCompose(s -> CompletableFuture.completedFuture(s + " world"));
            assertThat(future.get()).isEqualTo("hello world");
        }
    }

    @Nested
    class Parallel {
        // 类似于 js 中的 Promise.all()
        @Test
        @DisplayName("allOf")
        void test100() throws ExecutionException, InterruptedException {
            CompletableFuture<String> future1 = CompletableFuture.completedFuture("a");
            CompletableFuture<String> future2 = CompletableFuture.completedFuture("b");
            CompletableFuture<String> future3 = CompletableFuture.completedFuture("c");

            CompletableFuture futureAll = CompletableFuture.allOf(future1, future2, future3);

            futureAll.get();

            assertThat(future1.isDone()).isTrue();
            assertThat(future2.isDone()).isTrue();
            assertThat(future3.isDone()).isTrue();

            String res = Stream.of(future1, future2, future3)
                    .map(CompletableFuture::join)
                    .collect(Collectors.joining());

            assertThat(res).isEqualTo("abc");
        }

        // 类似于 js 中的 Promise.race()
        @Test
        @DisplayName("anyOf")
        void test200() throws ExecutionException, InterruptedException {
            CompletableFuture<String> future1 = CompletableFuture.completedFuture("a");
            CompletableFuture<String> future2 = CompletableFuture.completedFuture("b");
            CompletableFuture<String> future3 = CompletableFuture.completedFuture("c");

            CompletableFuture futureAll = CompletableFuture.anyOf(future1, future2, future3);

            Object res = futureAll.get();
            assertThat(res).isInstanceOf(String.class);
        }
    }

    @Nested
    class ExceptionHandling {
        @Test
        @DisplayName("completeExceptionally")
        void test100() {
            CompletableFuture<String> future = new CompletableFuture<>();

            future.completeExceptionally(new RuntimeException("gotcha"));

            assertThatThrownBy(future::get)
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("handle")
        void test200() throws ExecutionException, InterruptedException {
            CompletableFuture<String> future1 = CompletableFuture
                    .supplyAsync(() -> {
                        throw new RuntimeException("gotcha");
                    });

            CompletableFuture<String> future2 = CompletableFuture
                    .supplyAsync(() -> "foo");

            future1 = future1.handle((s, t) -> {
                assertThat(s).isNull();
                assertThat(t).isInstanceOf(RuntimeException.class);
                return "bar";
            });

            future2 = future2.handle((s, t) -> {
                assertThat(s).isEqualTo("foo");
                assertThat(t).isNull();
                return "bar";
            });

            assertThat(future1.get()).isEqualTo("bar");
            assertThat(future2.get()).isEqualTo("bar");
        }
    }
}
