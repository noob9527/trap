package cn.staynoob.trap.java.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("函数")
public class FunctionSpec {

    private <T> int genericSizeOf(T... args) {
        return args.length;
    }

    private int integerSizeOf(Integer... args) {
        return args.length;
    }

    /**
     * @throws Exception
     * @see cn.staynoob.trap.kotlin.basic.FunctionSpec.ParameterSpec#test300()
     */
    @Test
    @DisplayName("可变参数会自动展开")
    public void test100() throws Exception {
        Integer[] arr = {1, 2, 3};
        //在kotlin中，这里等于1
        assertThat(genericSizeOf(arr)).isEqualTo(3);
        //这里java不会展开数组, 而是将参数类型推断为 Serializable(Integer和Integer的共同祖先)
        assertThat(genericSizeOf(1, arr)).isEqualTo(2);

        // correct
        assertThat(integerSizeOf(arr)).isEqualTo(3);
        // incorrect
//        assertThat(integerSizeOf(1, arr)).isEqualTo(2);
    }
}
