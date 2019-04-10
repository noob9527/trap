package cn.staynoob.trap.java.guava.concurrent;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

public class FuturesSpec {

    @Nested
    class TimeoutsFutureSpec {
        @Test
        @DisplayName("basic usage")
        void test100() {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            SettableFuture<Boolean> settableFuture = SettableFuture.create();
            ListenableFuture<Boolean> future = Futures
                    .withTimeout(settableFuture, 100, TimeUnit.MILLISECONDS, executorService);

            assertThatThrownBy(future::get)
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(TimeoutException.class);
        }
    }

    @Nested
    class TransformSpec {
        @Test
        @DisplayName("synchronous transform")
        void test100() throws ExecutionException, InterruptedException {
            ListenableFuture<String> future1 = Futures.immediateFuture("foo");
            ListenableFuture<String> future2 = Futures.transform(future1, res -> res + "bar", MoreExecutors.directExecutor());

            assertThat(future2.get()).isEqualTo("foobar");
        }

        @Test
        @DisplayName("asynchronous transform")
        void test200() throws ExecutionException, InterruptedException {
            ListenableFuture<String> future1 = Futures.immediateFuture("foo");
            ListenableFuture<String> future2 = Futures.transformAsync(
                    future1,
                    res -> Futures.immediateFuture(res + "bar"),
                    MoreExecutors.directExecutor()
            );

            assertThat(future2.get()).isEqualTo("foobar");
        }

        @Test
        @DisplayName("lazy transform")
        void test300() throws ExecutionException, InterruptedException {
            AtomicBoolean executed1 = new AtomicBoolean(false);
            AtomicBoolean executed2 = new AtomicBoolean(false);
            ListenableFuture<String> raw = Futures.immediateFuture("foo");
            ListenableFuture<String> eager = Futures.transform(raw, res -> {
                executed1.set(true);
                return res + "bar";
            }, MoreExecutors.directExecutor());
            Future<String> lazy = Futures.lazyTransform(raw, (String res) -> {
                executed2.set(true);
                return res + "bar";
            });

            assertThat(executed1).isTrue();
            assertThat(executed2).isFalse();
            assertThat(lazy.get()).isEqualTo("foobar");
            assertThat(eager.get()).isEqualTo("foobar");
            assertThat(executed1).isTrue();
            assertThat(executed2).isTrue();
        }
    }
}
