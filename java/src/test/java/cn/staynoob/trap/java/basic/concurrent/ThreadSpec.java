package cn.staynoob.trap.java.basic.concurrent;

import cn.staynoob.trap.java.basic.utils.TestThread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ThreadSpec {
    @BeforeEach
    void setUp() {
        /*
          suppress the error log printed by default handler
          The `ThreadGroup` class implements the `Thread.UncaughtExceptionHandler` interface.
          Its `uncaughtException` method takes the following action:
          1. If the thread group has a parent, then the `uncaughtException` method of the parent group is called.
          2. Otherwise, if the `Thread.getDefaultUncaughtExceptionHandler` method returns a non-null handler, it is called.
          3. Otherwise, if the Throwable is an instance of `ThreadDeath`(by calling `Thread.stop` method), nothing happens.
          4. Otherwise, the name of the thread and the stack trace of the Throwable are printed on System.err.
          That is the stack trace that you have undoubtedly seen many times in your programs.
         */
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
        });
    }

    @Test
    @DisplayName("exceptions thrown by another exception cannot be caught directly")
    void test10() throws InterruptedException {
        Runnable runnable = () -> {
            throw new RuntimeException("gocha!");
        };

        Thread thread = new Thread(runnable);

        try {
            thread.start();
            thread.join();
        } catch (RuntimeException e) {
            throw new RuntimeException("this line won't be executed");
        }
    }

    @Test
    @DisplayName("catch exception by another thread by setting up an exception handler")
    void test20() throws InterruptedException {
        Thread.UncaughtExceptionHandler mock = Mockito.spy(Thread.UncaughtExceptionHandler.class);
        RuntimeException exception = new RuntimeException("gocha!");
        Runnable runnable = () -> {
            throw exception;
        };

        Thread thread = new Thread(runnable);
        thread.setUncaughtExceptionHandler(mock);

        thread.start();
        thread.join();
        Mockito.verify(mock).uncaughtException(thread, exception);
    }

    @Test
    @DisplayName("interrupted, isInterrupted should return false if a thread was not alive at the time of the interrupt was called")
    void test100() {
        Runnable runnable = () -> {
            assertThat(Thread.currentThread().isInterrupted()).isFalse();
            assertThat(Thread.interrupted()).isFalse();
        };
        TestThread testThread = new TestThread(runnable);
        testThread.getThread().interrupt();
        testThread.start();
        testThread.join();
    }

    @Test
    @DisplayName("calling the interrupted method should clear the interrupted status of the thread")
    void test200() {
        Runnable runnable = () -> {
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
            assertThat(Thread.interrupted()).isTrue();
            assertThat(Thread.currentThread().isInterrupted()).isFalse();
        };
        TestThread testThread = new TestThread(runnable);
        testThread.start();
        testThread.getThread().interrupt();
        testThread.join();
    }

    @Test
    @DisplayName("calling sleep/wait/join method when the interrupted status is set should throw InterruptedException")
    void test300() {
        Runnable runnable = () -> {
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
            assertThatThrownBy(() -> Thread.sleep(5000L))
                    .isInstanceOf(InterruptedException.class);
        };
        TestThread testThread = new TestThread(runnable);
        testThread.start();
        testThread.getThread().interrupt();
        testThread.join();
    }

    @Test
    @DisplayName("interrupt a blocked thread should throw InterruptedException")
    void test400() throws InterruptedException {
        Runnable runnable = () -> {
            assertThat(Thread.currentThread().isInterrupted()).isFalse();
            assertThatThrownBy(() -> Thread.sleep(5000L))
                    .isInstanceOf(InterruptedException.class);
        };
        TestThread testThread = new TestThread(runnable);
        testThread.start();
        Thread.sleep(100L);
        testThread.getThread().interrupt();
        testThread.join();
    }

    @Test
    @DisplayName("the interrupt status should be cleared if an InterruptedException is thrown")
    void test500() {
        Runnable runnable = () -> {
            assertThat(Thread.currentThread().isInterrupted()).isTrue();
            assertThatThrownBy(() -> Thread.sleep(5000L))
                    .isInstanceOf(InterruptedException.class);

            assertThat(Thread.currentThread().isInterrupted()).isFalse();
        };
        TestThread testThread = new TestThread(runnable);
        testThread.start();
        testThread.getThread().interrupt();
        testThread.join();

        assertThat(Thread.currentThread().isInterrupted()).isFalse();
    }

    @SuppressWarnings("deprecation")
    @Test
    @DisplayName("a thread cannot be interrupted while it is waiting to acquire a lock")
    void test600() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();

        TestThread thread = new TestThread(() -> {
            lock.lock();
            throw new RuntimeException("this line won't be executed");
        });
        thread.start();
        thread.getThread().interrupt();

        // kill the thread otherwise it never ends
        Thread.sleep(100L);
        thread.getThread().stop();
        assertThatThrownBy(thread::join)
                .isInstanceOf(ThreadDeath.class);
    }

    @SuppressWarnings("deprecation")
    @Test
    @DisplayName("using tryLock/lockInterruptibly to make a thread interruptible")
    void test700() {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();

        TestThread thread = new TestThread(() -> {
            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        thread.start();
        thread.getThread().interrupt();
        thread.join();
    }
}
