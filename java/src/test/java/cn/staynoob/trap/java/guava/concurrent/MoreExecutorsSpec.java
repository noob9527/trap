package cn.staynoob.trap.java.guava.concurrent;

import com.google.common.util.concurrent.MoreExecutors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

public class MoreExecutorsSpec {
    @Test
    @DisplayName("directExecutor execute code in the same thread")
    void test100() throws InterruptedException {
        Executor executor = MoreExecutors.directExecutor();
        CountDownLatch latch = new CountDownLatch(1);
        Thread thread = Thread.currentThread();

        executor.execute(() -> {
            assertThat(Thread.currentThread()).isEqualTo(thread);
            latch.countDown();
        });

        latch.await();
    }
}
