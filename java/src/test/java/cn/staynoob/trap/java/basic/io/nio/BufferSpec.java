package cn.staynoob.trap.java.basic.io.nio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class BufferSpec {
    @Test
    @DisplayName("initialize a buffer")
    void test100() {
        int BUFFER_SIZE = 10;
        IntBuffer buffer = IntBuffer.allocate(BUFFER_SIZE);

        assertThat(buffer.position()).isEqualTo(0);
        assertThat(buffer.limit()).isEqualTo(BUFFER_SIZE);
        assertThat(buffer.capacity()).isEqualTo(BUFFER_SIZE);

        assertThat(buffer.remaining()).isEqualTo(BUFFER_SIZE);
    }

    @Test
    @DisplayName("general pattern to put things into a buffer")
    void test200() {
        int BUFFER_SIZE = 10;
        IntBuffer buffer = IntBuffer.allocate(BUFFER_SIZE);

        int[] expected = new int[BUFFER_SIZE];
        int i = 0;
        while (buffer.hasRemaining()) {
            expected[i] = i;
            buffer.put(i++);
        }

        assertThat(buffer.remaining()).isEqualTo(0);
        assertThat(buffer.array()).containsExactly(expected);
    }

    @Test
    @DisplayName("general pattern to get things from a buffer")
    void test300() {
        int BUFFER_SIZE = 10;
        int[] arr = new int[BUFFER_SIZE];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }

        IntBuffer buffer = IntBuffer.wrap(arr);

        int expected = 0;
        while (buffer.hasRemaining()) {
            assertThat(buffer.get()).isEqualTo(expected++);
        }

        assertThat(buffer.position()).isEqualTo(10);
    }


    @Test
    @DisplayName("flip should set the limit to the current position and the position to 0")
    void test400() {
        int BUFFER_SIZE = 10;
        IntBuffer buffer = IntBuffer.allocate(BUFFER_SIZE);

        int i = 0;
        int expected = 5;
        while (i < expected) {
            buffer.put(i++);
        }

        assertThat(buffer.position()).isEqualTo(expected);
        assertThat(buffer.limit()).isEqualTo(BUFFER_SIZE);

        buffer.flip();

        assertThat(buffer.position()).isEqualTo(0);
        assertThat(buffer.limit()).isEqualTo(expected);
    }

    @Test
    @DisplayName("rewind should set the position to 0 and leave the limit unchanged")
    void test500() {
        int BUFFER_SIZE = 10;
        IntBuffer buffer = IntBuffer.allocate(BUFFER_SIZE);

        int i = 0;
        int expected = 5;
        while (i < expected) {
            buffer.put(i++);
        }

        assertThat(buffer.position()).isEqualTo(expected);
        buffer.limit(expected);

        buffer.rewind();

        assertThat(buffer.position()).isEqualTo(0);
        assertThat(buffer.limit()).isEqualTo(expected);
    }
}
