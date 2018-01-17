package cn.staynoob.trap.java.effectivejava.chapter05.item25;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GenericArrayTest {
    @SuppressWarnings({"unused", "UnusedAssignment"})
    @Test
    @DisplayName("arrays are covariant")
    void test100() {
        Number[] arr1;
        Integer[] arr2 = new Integer[1];
        arr1 = arr2;
    }
}
