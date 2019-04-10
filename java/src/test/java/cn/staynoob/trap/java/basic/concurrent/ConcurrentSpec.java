package cn.staynoob.trap.java.basic.concurrent;

import cn.staynoob.trap.java.basic.testutil.TestThread;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConcurrentSpec {

    @Nested
    class MutableVariableTest {

        class Demo {
            private boolean flag1 = false;
            private volatile boolean flag2 = false;
        }

        @Disabled("this case aims to demonstrate the usage of volatile keyword, however, it has a chance to fail")
        @SuppressWarnings("deprecation")
        @Test
        void test100() throws InterruptedException {
            Demo demo = new Demo();

            TestThread thread = new TestThread(() -> {
                @SuppressWarnings("unused")
                int i = 0;
                // some how we need this loop to trigger the issue
                while (!demo.flag1) i++;
                throw new RuntimeException("this line won't be executed");
            });

            thread.start();
            Thread.sleep(100L);
            demo.flag1 = true;
            Thread.sleep(100L);

            thread.getThread().stop();
            assertThatThrownBy(thread::join)
                    .isInstanceOf(ThreadDeath.class);
        }

        @Test
        void test200() throws InterruptedException {
            Demo demo = new Demo();

            TestThread thread = new TestThread(() -> {
                @SuppressWarnings("unused")
                int i = 0;
                while (!demo.flag2) i++;
                throw new RuntimeException("this line will be executed");
            });

            thread.start();
            Thread.sleep(100L);
            demo.flag2 = true;

            assertThatThrownBy(thread::join)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("executed");
        }
    }


}
