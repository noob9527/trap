package cn.staynoob.trap.java.basic.concurrent;

import cn.staynoob.trap.java.basic.utils.TestThread;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AtomicSpec {
    private class Demo1 {
        private volatile Integer value1 = 0;
        private AtomicInteger value2 = new AtomicInteger(0);
    }

    @Test
    @Disabled("this case aims to demonstrate the usage of AtomicInteger, however, it has a chance to fail")
    void test100() {
        int threadCount = 200;
        Demo1 demo1 = new Demo1();
        TestThread[] threads = new TestThread[threadCount];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new TestThread(() -> {
                demo1.value1++;
                demo1.value2.incrementAndGet();
            });
        }

        for (TestThread thread : threads) {
            thread.start();
        }
        for (TestThread thread : threads) {
            thread.join();
        }

        assertThat(demo1.value1).isLessThan(threadCount);
        assertThat(demo1.value2.get()).isEqualTo(threadCount);
    }

    private class Demo2 {
        private volatile AtomicInteger maximum1 = new AtomicInteger(0);
        private volatile AtomicInteger maximum2 = new AtomicInteger(0);
    }

    @Test
    @Disabled("this case aims to demonstrate the usage of AtomicInteger, however, it has a chance to fail")
    void test200() {
        int threadCount = 1000;
        Demo2 demo2 = new Demo2();
        TestThread[] threads = new TestThread[threadCount];

        for (int i = 0; i < threads.length; i++) {
            int v = i;
            threads[i] = new TestThread(() -> {
                // not atomic
                demo2.maximum1.set(Math.max(demo2.maximum1.get(), v));
                // atomic
                demo2.maximum2.accumulateAndGet(v, Math::max);

                synchronized (demo2) {
                    if (demo2.maximum1.get() < v) {
                        throw new RuntimeException("gotcha1");
                    }

                    if (demo2.maximum2.get() < v) {
                        throw new RuntimeException("gotcha2");
                    }
                }
            });
        }

        for (TestThread thread : threads) {
            thread.start();
        }
        assertThatThrownBy(() -> {
            for (TestThread thread : threads) {
                thread.join();
            }
        }).hasMessage("gotcha1");
    }
}
