package cn.staynoob.trap.kotlin.basic

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("函数")
class FunctionSpec {

    @DisplayName("参数")
    @Nested
    @Suppress("RemoveExplicitTypeArguments")
    inner class ParameterSpec {
        @Test
        @DisplayName("如果在调用函数时指明了参数名称，那该参数后面的所有参数都需要指定名称")
        fun test100() {
            fun fn(foo: Any, bar: Any, baz: Any, qux: Any) = listOf<Any>(foo, bar, baz, qux)
            // correct
            val list1 = fn(1, 2, 3, 4)
            val list2 = fn(1, 2, baz = 3, qux = 4)
            val list3 = fn(1, 2, qux = 4, baz = 3)
            assertThat(list1 == list2).isTrue()
            assertThat(list2 == list3).isTrue()
            // incorrect
//            fn(1, 2, baz = 3, 4)
        }

        @Test
        @DisplayName("如果不使用命名参数，则只允许省略排在末尾的参数")
        fun test200() {
            // 因此，如果希望调用者不使用命名参数就能使用默认参数，则应该尽量把带默认值的参数放在参数列表最后
            fun fn(foo: Any = 1, bar: Any = 2, baz: Any = 3, qux: Any) = listOf<Any>(foo, bar, baz, qux)
            // correct
            fn(qux = 4)
            // incorrect
//            fn(1, 2, 3)
        }

        /**
         * @see cn.staynoob.trap.java.basic.FunctionSpec.test100
         */
        @Test
        @DisplayName("不同于java,kotlin中的可变参数不会自动展开数组")
        fun test300() {
            fun <T> sizeOf(vararg element: T): Int {
                return element.size
            }

            val arr = arrayOf(1, 2, 3)
            assertThat(sizeOf(arr)).isEqualTo(1)
            assertThat(sizeOf(1, arr)).isEqualTo(2)
            assertThat(sizeOf(*arr)).isEqualTo(3)
            assertThat(sizeOf(1, *arr)).isEqualTo(4)
        }
    }

    @Test
    @DisplayName("只有表达式函数体可以省略返回值类型")
    fun test100() {
        fun fn1() = 1
        // correct
        fun fn2(): Int {
            return 2
        }
        // incorrect
//        fun fn3() {
//            return 3
//        }
    }

    @Test
    @DisplayName("kotlin中函数可以访问并修改外部作用域的值？")
    fun test200() {
        var x = 0
        fun fn1() = ++x
        val fn2= { ++x }
        fn1()
        assertThat(x).isEqualTo(1)
        fn2()
        assertThat(x).isEqualTo(2)
    }

    @Test
    @DisplayName("如果方法只有一个参数，可以考虑中缀调用")
    fun test2000() {
        infix fun Int.add(other: Int): Int = this + other
        assertThat(1.add(2))
                .isEqualTo(1 add 2)
                .isEqualTo(3)
    }
}