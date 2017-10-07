package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @see cn.staynoob.trap.kotlin.basic.GenericSpec
 */
@DisplayName("泛型")
public class GenericSpec {
    @Test
    @DisplayName("java泛型是不变(invariant)的")
    void test100() {
        // incorrect
//        List<Integer> intList = new ArrayList<Number>();
//        List<Number> numList = new ArrayList<Integer>();
    }

    /**
     * Collection<? extends T> 是 Collection<T> 的子类型（协变）
     * Collection<? super T> 是 Collection<T> 的父类型（逆变）
     *
     * @see cn.staynoob.trap.kotlin.basic.GenericSpec.VarianceSpec.TypeProjectionSpec#test100$test_sources_for_module_kotlin_test
     * @param src  允许生产，但不允许消费
     * @param dest 允许消费，但不允许生产
     * @param <T>
     */
    private <T> void copyData(
            Collection<? extends T> src,
            Collection<? super T> dest
    ) {
        dest.addAll(src);
    }

    /**
     * 由于java不允许声明处型变(Declaration-site variance)
     * 因此 Supplier<Integer> 不是 Supplier<Number> 的子类
     * 同理 Consumer<Number> 也不是 Consumer<Integer> 的子类
     * 即便这是完全安全，且合理的
     * 在java中如果api要对所有的Supplier<Number>可用
     * 只能将接收的类型声明为Supplier<? extends Number>
     *
     * @see cn.staynoob.trap.kotlin.basic.GenericSpec.VarianceSpec
     */
    @Test
    @DisplayName("java只允许在“使用点变形”")
    void test200() {
        List<Number> numList = new ArrayList<>();
        List<Integer> intList = Arrays.asList(1, 2, 3);
        copyData(intList, numList);
        assertThat(numList).containsExactly(1, 2, 3);
    }
}
