package cn.staynoob.trap.java.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ExceptionSpec {
    @Test
    @DisplayName("should throw exception even within a try-finally block")
    void test100() {
        Assertions.assertThatThrownBy(() -> {
            //noinspection EmptyFinallyBlock
            try {
                throw new RuntimeException("gotcha");
            } finally {
            }
        }).isInstanceOf(RuntimeException.class);
    }
}
