package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class CollectionSpec {

    @Nested
    class StackSpec {
        @Test
        @DisplayName("java stack iterates elements in wrong way")
        void test100() {
            Stack<Integer> stack = new Stack<>();

            stack.push(1);
            stack.push(2);
            stack.push(3);

            assertThat(stack.peek()).isEqualTo(3);

            List<Integer> list = new ArrayList<>(stack);

            // should be 3, 2, 1 by definition of a Stack
            assertThat(list).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("Deque might be an alternatives")
        void test200() {
            Deque<Integer> stack = new ArrayDeque<>();

            stack.push(1);
            stack.push(2);
            stack.push(3);

            assertThat(stack.peek()).isEqualTo(3);

            List<Integer> list = new ArrayList<>(stack);

            // should be 3, 2, 1 by definition of a Stack
            assertThat(list).containsExactly(3, 2, 1);
        }
    }
}
