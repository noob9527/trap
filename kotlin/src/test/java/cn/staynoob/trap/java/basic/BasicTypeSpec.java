package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

/**
 * @see Class#isPrimitive()
 */
@SuppressWarnings("ALL")
@DisplayName("基础类型")
public class BasicTypeSpec {

    @Test
    @DisplayName("java的原始数据类型之间可能发生隐式类型转换")
    void test100() {
        // correct
        int aInt = 1;
        long aLong = aInt;
        // incorrect
//        Integer intWrapper = 1;
//        Long LongWrapper = intWrapper;
    }

    @Test
    @DisplayName("使用Void作为类型参数来避免返回值")
    void test200() {
        new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                return null;
            }
        };
    }
}
