package cn.staynoob.trap.java.basic.concurrent

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ScheduledExecutorSpec {
    @Nested
    inner class HandleUncaughtExceptionInScheduledExecutor {
        @Test
        @DisplayName("executor silently ignore exception")
        fun test100() {
            val executor = Executors.newSingleThreadScheduledExecutor()
            executor.schedule({
                error("gotcha")
            }, 100L, TimeUnit.MILLISECONDS)

            var executed = false

            executor.schedule({
                executed = true
            }, 100L, TimeUnit.MILLISECONDS)

            executor.shutdown()
            executor.awaitTermination(200L, TimeUnit.MILLISECONDS)
            assertThat(executed).isTrue()
        }

        @Test
        @DisplayName("executor silently ignore exception, even uncaught exception handler cannot catch it")
        fun test200() {
            val executor = Executors.newSingleThreadScheduledExecutor {
                val thread = Thread(it)
                thread.setUncaughtExceptionHandler { _, e ->
                    println(e.stackTrace)
                }
                thread
            }
            executor.schedule({
                error("gotcha")
            }, 100L, TimeUnit.MILLISECONDS)
            executor.shutdown()
            executor.awaitTermination(200L, TimeUnit.MILLISECONDS)
        }

        @Test
        @DisplayName("use future.get() to catch exception")
        fun test300() {
            val exception = IllegalStateException("gotcha")
            val executor = Executors.newSingleThreadScheduledExecutor()
            val future = executor.schedule({
                throw exception
            }, 100L, TimeUnit.MILLISECONDS)
            assertThatThrownBy { future.get() }
                    .isInstanceOf(ExecutionException::class.java)
                    .hasCause(exception)
        }
    }
}
