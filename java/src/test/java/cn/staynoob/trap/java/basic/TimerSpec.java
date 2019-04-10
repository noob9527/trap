package cn.staynoob.trap.java.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Note:
 * <p>Corresponding to each <tt>Timer</tt> object is a single background
 * thread that is used to execute all of the timer's tasks, sequentially.
 * Timer tasks should complete quickly.  If a timer task takes excessive time
 * to complete, it "hogs" the timer's task execution thread.  This can, in
 * turn, delay the execution of subsequent tasks, which may "bunch up" and
 * execute in rapid succession when (and if) the offending task finally
 * completes.
 * <p>Java 5.0 introduced the {@code java.util.concurrent} package and
 * one of the concurrency utilities therein is the {@link
 * java.util.concurrent.ScheduledThreadPoolExecutor
 * ScheduledThreadPoolExecutor} which is a thread pool for repeatedly
 * executing tasks at a given rate or delay.  It is effectively a more
 * versatile replacement for the {@code Timer}/{@code TimerTask}
 * combination, as it allows multiple service threads, accepts various
 * time units, and doesn't require subclassing {@code TimerTask} (just
 * implement {@code Runnable}).  Configuring {@code
 * ScheduledThreadPoolExecutor} with one thread makes it equivalent to
 * {@code Timer}.
 * Reference
 * https://www.baeldung.com/java-timer-and-timertask
 */
public class TimerSpec {
    // analogy to setTimeout in js
    // this case aims to demonstrate the usage of Timer, however, it has a chance to fail
    @Test
    @DisplayName("schedule a task once")
    void test100() throws InterruptedException {
        AtomicBoolean executed = new AtomicBoolean(false);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                executed.set(true);
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 100L);

        Assertions.assertThat(executed).isFalse();

        Thread.sleep(200L);

        Assertions.assertThat(executed).isTrue();
    }

    // analogy to setInterval in js
    // this case aims to demonstrate the usage of Timer, however, it has a chance to fail
    @Test
    @DisplayName("schedule a repeated task at an interval")
    void test200() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                counter.incrementAndGet();
            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(task, 0L, 100L);

        Thread.sleep(500L);

        Assertions.assertThat(counter).hasValueBetween(3, 7);
    }

    @Test
    @DisplayName("schedule a task twice should throw IllegalStateException")
    void test300() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 100);
        Assertions.assertThatThrownBy(() -> {
            timer.schedule(task, 100);
        }).isInstanceOf(IllegalStateException.class);
    }

    @Nested
    class Cancellation {
        @Test
        @DisplayName("Cancel the TimerTask (one time execution)")
        void test100() throws InterruptedException {
            AtomicBoolean executed = new AtomicBoolean(false);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    executed.set(true);
                }
            };

            Timer timer = new Timer();
            timer.schedule(task, 200);
            task.cancel();

            Thread.sleep(300);
            Assertions.assertThat(executed).isFalse();
        }

        @Test
        @DisplayName("Cancel the TimerTask (repeated execution)")
        void test110() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger(0);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    counter.incrementAndGet();
                    cancel();
                }
            };

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(task, 0L, 100L);

            Thread.sleep(500L);

            Assertions.assertThat(counter).hasValue(1);
        }

        @Test
        @DisplayName("Cancel the Timer By calling the Timer.cancel() method on a Timer object")
        void test200() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger(0);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    counter.incrementAndGet();
                }
            };

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(task, 0L, 100L);
            Thread.sleep(200L);

            Assertions.assertThat(counter).hasValueBetween(0, 3);

            timer.cancel();
            Thread.sleep(200L);

            Assertions.assertThat(counter).hasValueBetween(0, 3);
        }

        @SuppressWarnings("Duplicates")
        @Test
        @DisplayName("a cancelled task cannot be rescheduled")
        void test300() {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 100);
            task.cancel();
            Assertions.assertThatThrownBy(() -> timer.schedule(task, 100))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @SuppressWarnings("Duplicates")
        @DisplayName("if a timer is already cancelled, it won't able to schedule any tasks")
        void test400() {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 100);
            timer.cancel();
            Assertions.assertThatThrownBy(() -> timer.schedule(task, 100))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class ExecutorService {
        @Test
        @DisplayName("run a repeated task at a specified interval")
        void test100() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger(0);
            Runnable runnable = counter::incrementAndGet;
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(runnable, 0, 100, TimeUnit.MILLISECONDS);

            Thread.sleep(500L);
            Assertions.assertThat(counter).hasValueBetween(3, 7);
        }
    }


    @Nested
    class ExceptionHandling {
        @Test
        @Disabled("stop printing stacktrace to the terminal")
        @DisplayName("runtime exception will kill the thread, so that subsequent task won't be executed")
        void test100() throws InterruptedException {
            AtomicInteger counter = new AtomicInteger(0);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    counter.incrementAndGet();
                    throw new RuntimeException("gotcha");
                }
            };

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(task, 0L, 100L);

            Thread.sleep(500L);

            Assertions.assertThat(counter).hasValue(1);
        }
    }
}
