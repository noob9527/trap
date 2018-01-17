package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NumberSpec {
    @Test
    @DisplayName("Math.abs(Integer.MIN_VALUE) is still Integer.MIN_VALUE")
    void test100() {
        // Integer.MAX_VALUE is 2^31 - 1
        // Integer.MIN_VALUE is -2^31
        assertThat(Math.abs(Integer.MIN_VALUE))
                .isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    @DisplayName("auto-unboxed null reference will get NullPointerException")
    void test200() {
        final Integer a = null;
        assertThatThrownBy(() -> {
            int b = a;
        }).isInstanceOf(NullPointerException.class);
    }
}
