package cn.staynoob.trap.java;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class FunctionSpec {

    private <T> List<T> genericListOf(T... args) {
        return Arrays.asList(args);
    }

    @Test
    public void varargsShouldAutoExpandArray() throws Exception {
        Integer[] arr = {1, 2, 3};
        assertThat(genericListOf(arr).size()).isEqualTo(3);
        assertThat(genericListOf(1, arr).size()).isEqualTo(2);
    }
}
