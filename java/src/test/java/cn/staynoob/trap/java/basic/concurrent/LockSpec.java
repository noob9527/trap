package cn.staynoob.trap.java.basic.concurrent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class LockSpec {

    @Test
    @DisplayName("check if a thread holds lock with thread.holdsLock method")
    void test100() {
        Object lock1 = new Object();
        Object lock2 = new Object();

        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (lock1) {
            assertThat(Thread.holdsLock(lock1)).isTrue();
            assertThat(Thread.holdsLock(lock2)).isFalse();
        }
    }

    @Nested
    class ReadWriteLockSpec {
        @Test
        @DisplayName("shared lock")
        void test100() throws InterruptedException {
            ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
            Lock readLock = readWriteLock.readLock();

            AtomicBoolean acquired = new AtomicBoolean(false);

            readLock.lock();

            Thread thread = new Thread(() -> acquired.set(readLock.tryLock()));
            thread.start();
            thread.join();

            assertThat(acquired).isTrue();
        }

        @Test
        @DisplayName("mutual-exclusive lock")
        void test200() throws InterruptedException {
            ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
            Lock writeLock = readWriteLock.writeLock();
            Lock readLock = readWriteLock.readLock();

            AtomicBoolean acquired = new AtomicBoolean(false);

            writeLock.lock();

            Thread thread = new Thread(() -> acquired.set(readLock.tryLock()));
            thread.start();
            thread.join();

            assertThat(acquired).isFalse();
        }
    }

    /**
     * basically, they are all some cases of circular wait
     */
    @Nested
    class DeadLockSpec {

        @Test
        @DisplayName("findDeadLockedThreads return null if cannot find any")
        void test50() {
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            long[] threadIds = threadMXBean.findDeadlockedThreads();

            assertThat(threadIds).isNull();
        }

        @SuppressWarnings("Duplicates")
        @Test
        @DisplayName("create dead lock with locks")
        void test100() throws InterruptedException {
            ReentrantLock lock1 = new ReentrantLock();
            ReentrantLock lock2 = new ReentrantLock();

            Thread thread1 = new Thread(() -> {
                try {
                    lock1.lockInterruptibly();
                    Thread.sleep(100L);
                    lock2.lockInterruptibly();
                } catch (InterruptedException ignored) {
                }
            });

            Thread thread2 = new Thread(() -> {
                try {
                    lock2.lockInterruptibly();
                    Thread.sleep(100L);
                    lock1.lockInterruptibly();
                } catch (InterruptedException ignored) {
                }
            });

            thread1.start();
            thread2.start();

            Thread.sleep(200L);

            assertThat(thread1.getState()).isEqualTo(Thread.State.WAITING);
            assertThat(thread2.getState()).isEqualTo(Thread.State.WAITING);

            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            long[] threadIds = threadMXBean.findDeadlockedThreads();

            assertThat(threadIds).isNotNull();

            List<Long> threadIdList = Arrays.stream(threadIds)
                    .boxed()
                    .collect(Collectors.toList());

            assertThat(threadIdList)
                    .containsExactlyInAnyOrder(thread1.getId(), thread2.getId());

            thread1.interrupt();
            thread2.interrupt();
        }

        @SuppressWarnings("Duplicates")
        @Test
        @DisplayName("create dead lock with condition")
        void test200() throws InterruptedException {
            AtomicInteger account1 = new AtomicInteger(100);
            AtomicInteger account2 = new AtomicInteger(100);

            ReentrantLock lock = new ReentrantLock();
            Condition condition = lock.newCondition();

            Thread thread1 = new Thread(() -> {
                try {
                    lock.lockInterruptibly();
                    while (account1.get() - 200 < 0) {
                        condition.await();
                        account1.set(account1.get() - 200);
                        account2.set(account2.get() + 200);
                        condition.notifyAll();
                    }
                } catch (InterruptedException ignored) {
                }
            });

            Thread thread2 = new Thread(() -> {
                try {
                    lock.lockInterruptibly();
                    while (account2.get() - 200 < 0) {
                        condition.await();
                        account1.set(account1.get() + 200);
                        account2.set(account2.get() - 200);
                        condition.notifyAll();
                    }
                } catch (InterruptedException ignored) {
                }
            });

            thread1.start();
            thread2.start();

            Thread.sleep(200L);

            assertThat(thread1.getState()).isEqualTo(Thread.State.WAITING);
            assertThat(thread2.getState()).isEqualTo(Thread.State.WAITING);

            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            long[] threadIds = threadMXBean.findDeadlockedThreads();

            // Note that ThreadMXBean cannot detect deadlock in this case
            assertThat(threadIds).isNull();

            thread1.interrupt();
            thread2.interrupt();
        }

        @SuppressWarnings("Duplicates")
        @Test
        @DisplayName("live lock")
        void test300() throws InterruptedException {
            ReentrantLock lock1 = new ReentrantLock();
            ReentrantLock lock2 = new ReentrantLock();

            Thread thread1 = new Thread(() -> {
                try {
                    Boolean holdLock1;
                    Boolean holdLock2;
                    while (true) {
                        holdLock1 = lock1.tryLock();
                        if (!holdLock1) {
                            continue;
                        }
//                        System.out.println("thread1 success to acquire lock1");
                        Thread.sleep(100);
                        holdLock2 = lock2.tryLock();
                        if (!holdLock2) {
//                            System.out.println("thread1 fail to acquire lock2, unlock lock1");
                            lock1.unlock();
                            continue;
                        }
                        break;
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    if (lock1.isHeldByCurrentThread()) {
                        lock1.unlock();
                    }
                    if (lock2.isHeldByCurrentThread()) {
                        lock2.unlock();
                    }
                }
            });

            Thread thread2 = new Thread(() -> {
                try {
                    Boolean holdLock1;
                    Boolean holdLock2;
                    while (true) {
                        holdLock2 = lock2.tryLock();
                        if (!holdLock2) {
                            continue;
                        }
//                        System.out.println("thread2 success to acquire lock2");
                        Thread.sleep(100);
                        holdLock1 = lock1.tryLock();
                        if (!holdLock1) {
//                            System.out.println("thread2 fail to acquire lock1, unlock lock2");
                            lock2.unlock();
                            continue;
                        }
                        break;
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    if (lock1.isHeldByCurrentThread()) {
                        lock1.unlock();
                    }
                    if (lock2.isHeldByCurrentThread()) {
                        lock2.unlock();
                    }
                }
            });

            thread1.start();
            thread2.start();

            Thread.sleep(200);

            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            long[] threadIds = threadMXBean.findDeadlockedThreads();

            // ThreadMXBean fail to locate live lock in this case
            assertThat(threadIds).isNull();

            thread1.interrupt();

            thread1.join();
            thread2.join();

        }

        /**
         * Effective Java Item79:
         * Never cede control to the client within a synchronized method or block!
         * <p>
         * in this case, thread1 run alien method in a synchronized block
         * the alien code set up a new thread, and run some code which acquire the same lock
         * then we get a deadlock
         */
        @Test
        @DisplayName("create deadlock by running alien method in synchronized block")
        void test400() throws InterruptedException {
            class Listenable {
                Runnable callback;

                public Listenable() {
                }

                synchronized void run() {
                    callback.run();
                }

                synchronized void otherMethod() {
                }
            }
            Listenable listenable = new Listenable();

            Thread thread1 = new Thread(listenable::run);
            Thread thread2 = new Thread(listenable::otherMethod);

            listenable.callback = () -> {
                thread2.start();
                try {
                    thread2.join();
                } catch (InterruptedException ignored) {
                }
            };
            thread1.start();

            Thread.sleep(200);

            assertThat(thread1.getState()).isEqualTo(Thread.State.WAITING);
            assertThat(thread2.getState()).isEqualTo(Thread.State.BLOCKED);

            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            long[] threadIds = threadMXBean.findDeadlockedThreads();

            // Again, ThreadMXBean cannot detect deadlock in this case
            assertThat(threadIds).isNull();

            thread1.interrupt();
            thread2.interrupt();
        }
    }
}
