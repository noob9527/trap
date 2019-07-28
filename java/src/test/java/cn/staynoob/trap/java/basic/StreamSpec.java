package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamSpec {

    @Test
    @DisplayName("use identity value e such that e op x = x ")
    void test100() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        @SuppressWarnings("Convert2MethodRef")
        Integer sum = list.stream().reduce(0, (a, b) -> a + b);
        Integer product = list.stream().reduce(1, (a, b) -> a * b);

        assertThat(sum).isEqualTo(6);
        assertThat(product).isEqualTo(6);
    }

}
