package cn.staynoob.trap.java.basic.concurrent;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SynchronizerSpec {
    @Nested
    class CountDownLatchSpec {
        @Test
        @DisplayName("use CountDownLatch to convert asynchronous code to synchronous code")
        void test100() throws InterruptedException {
            CountDownLatch latch = new CountDownLatch(1);

            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(100L);
                    latch.countDown();
                } catch (InterruptedException ignored) {
                }
            });
            thread.start();

            latch.await();

            assertThat(thread.getState())
                    .isEqualTo(Thread.State.TERMINATED);
        }
    }

    @Nested
    class CyclicBarrierSpec {
        @Test
        @DisplayName("Basic usage of CyclicBarrier")
        void test100() throws InterruptedException, BrokenBarrierException {

            AtomicBoolean executed = new AtomicBoolean(false);
            AtomicInteger counter = new AtomicInteger(0);

            CyclicBarrier barrier = new CyclicBarrier(3, () -> executed.set(true));

            Runnable runnable = () -> {
                try {
                    counter.incrementAndGet();
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException ignored) {
                }
            };

            Thread thread1 = new Thread(runnable);
            Thread thread2 = new Thread(runnable);

            thread1.start();
            thread2.start();

            Thread.sleep(100);

            barrier.await();

            assertThat(executed).isTrue();
            assertThat(counter).hasValue(2);

            thread1.join();
            thread2.join();
        }
    }

    @Nested
    class ForkJoinSpec {
        @Test
        @DisplayName("divide and conquer algorithm via fork join")
        void test100() {
            class QuickSort<T extends Comparable<T>>
                    extends RecursiveTask<List<T>> {
                private final List<T> list;

                public QuickSort(
                        List<T> list
                ) {
                    this.list = list;
                }


                @Override
                protected List<T> compute() {
                    if (this.list.size() <= 1) return this.list;

                    T pivot = this.list.get(this.list.size() - 1);
                    List<T> leftList = list.stream()
                            .limit(this.list.size() - 1)
                            .filter(e -> e.compareTo(pivot) <= 0)
                            .collect(Collectors.toList());
                    List<T> rightList = list.stream()
                            .limit(this.list.size() - 1)
                            .filter(e -> e.compareTo(pivot) > 0)
                            .collect(Collectors.toList());
                    QuickSort<T> leftSort = new QuickSort<>(leftList);
                    QuickSort<T> rightSort = new QuickSort<>(rightList);

                    invokeAll(leftSort, rightSort);
                    List<T> leftResult = leftSort.join();
                    List<T> rightResult = rightSort.join();

                    List<T> result = new ArrayList<>(this.list.size());

                    result.addAll(leftResult);
                    result.add(pivot);
                    result.addAll(rightResult);

                    return result;
                }

            }

            List<Integer> input = Arrays.asList(1, 5, 3, 4, 2);
            QuickSort<Integer> sort = new QuickSort<>(input);

            assertThat(sort.invoke()).containsExactly(1, 2, 3, 4, 5);
        }
    }
}
