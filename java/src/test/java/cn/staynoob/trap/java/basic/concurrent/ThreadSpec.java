package cn.staynoob.trap.java.basic.concurrent;

import cn.staynoob.trap.java.basic.testutil.TestThread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.Set;
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

    @SuppressWarnings("Duplicates")
    @Test
    @DisplayName("call setDaemon after a thread has started should throw IllegalThreadStateException")
    void test100() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ignored) {
                }
            }
        });
        assertThat(thread.isDaemon()).isFalse();
        thread.setDaemon(true);
        assertThat(thread.isDaemon()).isTrue();

        thread.start();

        assertThatThrownBy(() -> thread.setDaemon(false))
                .isInstanceOf(IllegalThreadStateException.class);
    }

    @SuppressWarnings("Duplicates")
    @Test
    @DisplayName("call thread.start() twice should throw IllegalThreadStateException")
    void test200() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ignored) {
                }
            }
        });

        thread.start();
        thread.start();
    }

    @Nested
    class StaticMethodSpec {
        @Test
        @DisplayName("find a thread by its id")
        void test100() {
            Thread thread = new Thread(() -> {
            });
            thread.start();

            Set<Thread> threads = Thread.getAllStackTraces().keySet();
            Optional<Thread> optional = threads.stream()
                    .filter(t -> t.getId() == thread.getId())
                    .findFirst();
            assertThat(optional).hasValue(thread);
        }
    }

    @Nested
    class ExceptionHandling {
        @Test
        @DisplayName("exceptions thrown by another thread cannot be caught directly")
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
    }

    @Nested
    class ThreadState {

        /**
         * about thread state:
         * When a thread is blocked or waiting, it is temporarily inactive. It doesn't execute any code and consumes minimal resources. It is up to the thread scheduler to reactivate it. The details depend on how the inactive state was reached.
         * - When the thread tries to acquire an intrinsic object lock (but not a `Lock` in the `java.util.concurrent` library) that is currently held by another thread, it becomes blocked. The thread becomes unblocked when all other threads have relinquished the lock and the thread scheduler has allowed this thread to hold it.
         * - When the thread waits for another thread to notify the scheduler of a condition, it enters the waiting state. This happens by calling the `Object.wait` or `Thread.join` method, or by waiting for a Lock or Condition in the `java.util.concurrent` library. In practice, the difference between the blocked and waiting state is not significant.
         * - Several methods have a timeout parameter. Calling them causes the thread to enter the timed waiting state. This state persists either until the timeout expires or the appropriate notification has been received. Methods with timeout include `Thread.sleep` and the timed versions of `Object.wait`, `Thread.join`, `Lock.tryLock`, and `Condition.await`.
         */
        @SuppressWarnings("Duplicates")
        @Test
        @DisplayName("the differences between BLOCKED, WAITING")
        void test100() throws InterruptedException {
            Object lock1 = new Object();
            ReentrantLock lock2 = new ReentrantLock();

            Thread thread1 = new Thread(() -> {
                synchronized (lock1) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lock2.lock();
                }
            });

            Thread thread2 = new Thread(() -> {
                lock2.lock();
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock1) {
                }
            });

            thread1.start();
            thread2.start();

            Thread.sleep(200L);

            assertThat(thread1.getState()).isEqualTo(Thread.State.WAITING);
            assertThat(thread2.getState()).isEqualTo(Thread.State.BLOCKED);
        }
    }

    @Nested
    class Interruptability {
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

}
