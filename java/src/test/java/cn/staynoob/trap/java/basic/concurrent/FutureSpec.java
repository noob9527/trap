package cn.staynoob.trap.java.basic.concurrent;

import cn.staynoob.trap.java.basic.utils.TestThread;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FutureSpec {
    @Test
    @DisplayName("the computation will never start if the future is cancelled before the computation start")
    void test100() {
        FutureTask<Integer> task = new FutureTask<>(() -> {
            throw new RuntimeException("this line won't be executed");
        });
        TestThread thread = new TestThread(task);
        task.cancel(false);
        thread.start();
        thread.join();
    }

    @Test
    @DisplayName("if the corresponding thread is interrupted, task.get throws ExecutionException which violates its documentation")
    void test150() {
        FutureTask<Integer> task = new FutureTask<>(() -> {
            synchronized (this) {
                wait();
                return 0;
            }
        });
        Thread thread = new Thread(task);
        thread.start();
        thread.interrupt();
        assertThatThrownBy(task::get)
                .isInstanceOf(ExecutionException.class);
    }

    @Test
    @DisplayName("interrupting a computation by set the mayInterrupt parameter to true")
    void test200() throws InterruptedException {
        FutureTask<Integer> task = new FutureTask<>(() -> {
            int i = 0;
            while (!Thread.currentThread().isInterrupted()) {
                i++;
            }
            return i;
        });
        TestThread thread = new TestThread(task);
        thread.start();

        Thread.sleep(100L);

        task.cancel(true);
        assertThat(thread.getThread().isInterrupted()).isTrue();
    }
}
